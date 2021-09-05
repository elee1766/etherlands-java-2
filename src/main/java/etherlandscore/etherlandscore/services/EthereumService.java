package etherlandscore.etherlandscore.services;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.singleton.SettingsSingleton;
import etherlandscore.etherlandscore.state.write.WriteDistrict;
import org.bukkit.Bukkit;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.ThreadFiber;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class EthereumService extends ListenerClient {
    private final Channels channels;
    private final Fiber fiber;

    private final Web3j web3;
    private final District district;

    private final Map<String, String> settings = SettingsSingleton.getSettings().getSettings();

    private final Web3ClientVersion web3ClientVersion;
    private final String clientVersion;

    private BigInteger fromBlock;
    private final BigInteger lookback = new BigInteger(settings.get("lookback"));

    public EthereumService(Channels channels, Fiber fiber) throws Exception {
        super(channels, fiber);
        this.channels = channels;
        this.fiber = fiber;
        web3 = Web3j.build(new HttpService(settings.get("NodeUrl")));
        web3ClientVersion = web3.web3ClientVersion().send();
        clientVersion = web3ClientVersion.getWeb3ClientVersion();

        EthBlockNumber blockNumberRequest = web3.ethBlockNumber().send();
        Bukkit.getLogger().info("Lookback: " + lookback);
        fromBlock = blockNumberRequest.getBlockNumber().subtract(lookback);


        ClientTransactionManager txnManager = new ClientTransactionManager(web3, settings.get("txnManager"));
        ContractGasProvider gasProvider = new ContractGasProvider() {
            @Override
            public BigInteger getGasLimit(String contractFunc) {
                return getGasLimit();
            }

            @Override
            public BigInteger getGasLimit() {
                return BigInteger.TEN.pow(3);
            }

            @Override
            public BigInteger getGasPrice(String contractFunc) {
                return getGasPrice();
            }

            @Override
            public BigInteger getGasPrice() {
                return BigInteger.TEN;
            }
        };
        district = District.load(settings.get("contractAddress"), web3, txnManager, gasProvider);
        this.channels.ethers_command.subscribe(fiber, x -> {
            switch (x.getCommand()) {
                case ethers_query_nft -> plotTransfer((Integer) x.getArgs()[0], (Integer) x.getArgs()[1], (Integer) x.getArgs()[2]);
            }
        });
        queryHistory();
        listenHttp();
    }

    public void queryHistory() throws IOException {
        Bukkit.getLogger().info("Querying");
        EthBlockNumber blockNumberRequest = web3.ethBlockNumber().send();
        BigInteger toBlock = blockNumberRequest.getBlockNumber();

        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(fromBlock),
                DefaultBlockParameter.valueOf(toBlock),
                district.getContractAddress()
        ).addSingleTopic("0x5112ef0e7d99d6ff5fcfc318db540adef82456873e39b67493ce8cf18f2af76c");

        district.plotTransferEventFlowable(filter).doOnError(Throwable::printStackTrace).subscribe(txnEvent->{
            if(txnEvent==null) {
                return;
            }
            plotTransfer(txnEvent.origin_id.intValueExact(), txnEvent.target_id.intValue(), txnEvent.plotId.intValueExact());
        });

        district.transferEventFlowable(filter).doOnError(Throwable::printStackTrace).subscribe(txnEvent->{
            if(txnEvent==null) {
                return;
            }
            districtTransfer(txnEvent.from, txnEvent.to, txnEvent.tokenId.intValueExact());
        });

        fromBlock = toBlock;
    }

    private void plotTransfer(Integer origin_id, Integer target_id, Integer plot_id) {
        String owneraddr = district.ownerOf(BigInteger.valueOf(target_id)).sendAsync().handle((res, throwable) -> res).join();
        Bukkit.getLogger().info(owneraddr + " " + plot_id);

        Integer x = district.plot_x(BigInteger.valueOf(plot_id)).sendAsync().handle((res, throwable) -> res != null ? res.intValue() : Integer.MAX_VALUE).join();
        Integer z = district.plot_z(BigInteger.valueOf(plot_id)).sendAsync().handle((res, throwable) -> res != null ? res.intValue() : Integer.MAX_VALUE).join();
        if (x != 0 || z != 0) {
            if (owneraddr != null) {
                this.channels.master_command.publish(new Message<>(MasterCommand.plot_update_plot, plot_id, x, z, owneraddr));
            }
        }

        this.channels.master_command.publish(new Message<>(MasterCommand.district_update_district, target_id, plot_id, owneraddr));
        this.channels.master_command.publish(new Message<>(MasterCommand.district_remove_plot, context.getDistrict(origin_id), context.getPlot(plot_id)));
    }

    private void districtTransfer(String origin_address, String target_address, Integer district_id) {
        Bukkit.getLogger().info(origin_address + " -> " + target_address + ": " + district_id);

        WriteDistrict d = (WriteDistrict) context.getDistrict(district_id);
        if(d!=null){
            Set<Integer> pIds = d.getPlotIds();
            for(Integer id : pIds){
                Integer x = district.plot_x(BigInteger.valueOf(id)).sendAsync().handle((res, throwable) -> res != null ? res.intValue() : Integer.MAX_VALUE).join();
                Integer z = district.plot_z(BigInteger.valueOf(id)).sendAsync().handle((res, throwable) -> res != null ? res.intValue() : Integer.MAX_VALUE).join();
                if (x != 0 || z != 0) {
                    this.channels.master_command.publish(new Message<>(MasterCommand.plot_update_plot, id, x, z, target_address));
                }
            }
        }
        d.setOwnerAddress(target_address);
    }

    private void listenHttp() throws Exception {
        Fiber httpFiber = new ThreadFiber();
        HttpLinkServer server = new HttpLinkServer(channels,httpFiber);
        int port = Integer.valueOf(settings.get("webPort"));
        server.launch(port);
        Bukkit.getLogger().info("Etherlands web Server started on port " + port);
    }

    private Set<Integer> queryDistrictPlots(Integer districtId) {
        return null;
    }
}
