package etherlandscore.etherlandscore.services;

import com.sun.net.httpserver.HttpServer;
import etherlandscore.etherlandscore.fibers.Channels;
import etherlandscore.etherlandscore.fibers.Message;
import etherlandscore.etherlandscore.state.Plot;
import org.bukkit.Bukkit;
import org.jetlang.fibers.Fiber;
import org.jetlang.fibers.ThreadFiber;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;

public class EthereumService extends  ListenerClient {
    private final Channels channels;
    private final Fiber fiber;

    private final Web3j web3;
    private final LandPlot landPlot;

    private final Web3ClientVersion web3ClientVersion;
    private final String clientVersion;
    public EthereumService(Channels channels, Fiber fiber) throws IOException{
        super(channels, fiber);
        this.channels = channels;
        this.fiber = fiber;
        web3 = Web3j.build(new HttpService("http://owl.elee.bike:8546"));
        web3ClientVersion = web3.web3ClientVersion().send();
        clientVersion = web3ClientVersion.getWeb3ClientVersion();
        ClientTransactionManager txnManager= new ClientTransactionManager(web3, "0x5227a7404631Eb7De411232535E36dE8dad318f0");
        ContractGasProvider gasProvider = new ContractGasProvider() {
            @Override
            public BigInteger getGasPrice(String contractFunc) {
                return getGasPrice();
            }

            @Override
            public BigInteger getGasPrice() {
                return BigInteger.TEN;
            }

            @Override
            public BigInteger getGasLimit(String contractFunc) {
                return getGasLimit();
            }

            @Override
            public BigInteger getGasLimit() {
                return BigInteger.TEN.pow(3);
            }
        };
        landPlot = LandPlot.load("0x45072d88faea89dd42791808f8b491ab70b279fa",web3,txnManager,gasProvider);

        this.channels.master_command.subscribe(fiber, x-> {
            try {
                queryChunkId((Integer) x.getArgs()[0]);
            } catch (Exception e) {
                e.printStackTrace();
                Bukkit.getLogger().warning(e.getMessage());
            }
        });
        listenHttp();
    }

    private void listenHttp() throws IOException {
        String host = "localhost";
        int port = 25510;
        HttpServer server = HttpServer.create(new InetSocketAddress(host,port),0);
        Fiber httpFiber = new ThreadFiber();
        server.createContext("/test", new HttpClient(channels,httpFiber));
        server.setExecutor(httpFiber);
        server.start();
        httpFiber.start();
        Bukkit.getLogger().info("Etherlands web Server started on port "+port);
    }

    private void queryChunkId(Integer chunkId) {
        Integer x =  landPlot.chunk_x(BigInteger.valueOf(chunkId)).sendAsync().handle((res, throwable)-> res != null ? res.intValue() : Integer.MAX_VALUE).join();
        Integer z =  landPlot.chunk_y(BigInteger.valueOf(chunkId)).sendAsync().handle((res,throwable)-> res != null ? res.intValue() : Integer.MAX_VALUE).join();
        String owneraddr = landPlot.ownerOf(BigInteger.valueOf(chunkId)).sendAsync().handle((res,throwable)-> res).join();
        Bukkit.getLogger().info(owneraddr + " " + x.toString() + " " + z.toString());
        if(x != 0 || z != 0 ) {
            if (owneraddr != null) {
                this.channels.master_command.publish(new Message("plot_create_plot",new Plot(chunkId, x, z, owneraddr)));
            }
        }
    }




}
