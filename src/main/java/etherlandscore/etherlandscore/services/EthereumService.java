package etherlandscore.etherlandscore.services;

import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.singleton.SettingsSingleton;
import kotlin.Pair;
import org.bukkit.Bukkit;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.ThreadFiber;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

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
                case ethers_query_nft -> {
                    try {
                        update_district((Integer) x.getArgs()[0]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        listenHttp();
    }

    private void update_district(Integer district_id) throws Exception {
        Pair<String, Set<Integer>> district_info = EthProxyClient.view_district(district_id);
        String owneraddr = district_info.getFirst();
        Bukkit.getLogger().info(owneraddr + " " + district_id);
        this.channels.master_command.publish(
            new Message<>(MasterCommand.district_update_district, district_id, district_info.getSecond(), owneraddr));
    }

    public void update_districts() throws Exception {
        Pair<Set<Integer>,Integer> to_check = EthProxyClient.find_districts(this.fromBlock.intValueExact());
        for (Integer district_id : to_check.getFirst()) {
            this.update_district(district_id);
        }
        this.fromBlock = BigInteger.valueOf(to_check.getSecond());
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
