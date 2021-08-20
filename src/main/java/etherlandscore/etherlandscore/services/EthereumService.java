package etherlandscore.etherlandscore.services;

import com.sun.net.httpserver.HttpServer;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.MasterCommand;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.singleton.SettingsSingleton;
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
import java.net.InetSocketAddress;
import java.util.Map;

public class EthereumService extends ListenerClient {
    private final Channels channels;
    private final Fiber fiber;

    private final Web3j web3;
    private final LandPlot landPlot;

    private final Map<String, String> settings = SettingsSingleton.getSettings().getSettings();

    private final Web3ClientVersion web3ClientVersion;
    private final String clientVersion;

    private BigInteger currentBlock;
    private final BigInteger lookback = new BigInteger(settings.get("lookback"));

    public EthereumService(Channels channels, Fiber fiber) throws IOException {
        super(channels, fiber);
        this.channels = channels;
        this.fiber = fiber;
        web3 = Web3j.build(new HttpService(settings.get("NodeUrl")));
        web3ClientVersion = web3.web3ClientVersion().send();
        clientVersion = web3ClientVersion.getWeb3ClientVersion();

        EthBlockNumber blockNumberRequest = web3.ethBlockNumber().send();
        Bukkit.getLogger().info("Lookback: " + lookback);
        currentBlock = blockNumberRequest.getBlockNumber().subtract(lookback);


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
        landPlot = LandPlot.load(settings.get("contractAddress"), web3, txnManager, gasProvider);
        this.channels.ethers_command.subscribe(fiber, x -> {
            switch (x.getCommand()) {
                case ethers_query_nft -> queryChunkId((Integer) x.getArgs()[0]);
            }
        });
        listenHttp();
        queryLastBlock();
    }

    public void queryLastBlock() throws IOException {
        Bukkit.getLogger().info("Querying");
        EthBlockNumber blockNumberRequest = web3.ethBlockNumber().send();
        BigInteger newBlock = blockNumberRequest.getBlockNumber();
        EthFilter filter = new EthFilter(
                DefaultBlockParameter.valueOf(currentBlock),
                DefaultBlockParameter.valueOf(newBlock),
                landPlot.getContractAddress()
        ).addSingleTopic("0xddf252ad1be2c89b69c2b068fc378daa952ba7f163c4a11628f55a4df523b3ef");
        landPlot.transferEventFlowable(filter).doOnError(Throwable::printStackTrace).subscribe(txnEvent->{
            if(txnEvent==null) {
                return;
            }
            queryChunkId(txnEvent.tokenId.intValueExact());
        });
        currentBlock = newBlock;
    }

    private void listenHttp() throws IOException {
        String host = "localhost";
        int port = 25510;
        HttpServer server = HttpServer.create(new InetSocketAddress(host, port), 0);
        Fiber httpFiber = new ThreadFiber();
        server.createContext("/test", new HttpLinkServer(channels, httpFiber));
        server.setExecutor(httpFiber);
        server.start();
        httpFiber.start();
        Bukkit.getLogger().info("Etherlands web Server started on port " + port);
    }

    private void queryChunkId(Integer chunkId) {
        Integer x = landPlot.chunk_x(BigInteger.valueOf(chunkId)).sendAsync().handle((res, throwable) -> res != null ? res.intValue() : Integer.MAX_VALUE).join();
        Integer z = landPlot.chunk_z(BigInteger.valueOf(chunkId)).sendAsync().handle((res, throwable) -> res != null ? res.intValue() : Integer.MAX_VALUE).join();
        String owneraddr = landPlot.ownerOf(BigInteger.valueOf(chunkId)).sendAsync().handle((res, throwable) -> res).join();
        Bukkit.getLogger().info(owneraddr + " " + x.toString() + " " + z.toString());
        if (x != 0 || z != 0) {
            if (owneraddr != null) {
                this.channels.master_command.publish(new Message<>(MasterCommand.plot_update_plot, chunkId, x, z, owneraddr));
            }
        }
    }
}
