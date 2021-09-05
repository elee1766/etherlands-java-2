package etherlandscore.etherlandscore.services;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.math.BigInteger;
import java.util.Map;
import java.util.Set;

public class EthProxyClient {
    private final Web3j web3;
    private final Map<String, String> settings = SettingsSingleton.getSettings().getSettings();
    private final Web3ClientVersion web3ClientVersion;
    private BigInteger fromBlock;
    private BigInteger currentBlock;
    private final BigInteger lookback = new BigInteger(settings.get("lookback"));
    private String blockCommand;
    private String districtCommand;

    public EthProxyClient() throws Exception {
        web3 = Web3j.build(new HttpService(settings.get("NodeUrl")));
        web3ClientVersion = web3.web3ClientVersion().send();

        EthBlockNumber blockNumberRequest = web3.ethBlockNumber().send();
        Bukkit.getLogger().info("Lookback: " + lookback);
        currentBlock = blockNumberRequest.getBlockNumber();
        fromBlock = currentBlock.subtract(lookback);
        blockCommand =
            "curl -X GET https://localhost:10100/block/"+fromBlock;
        districtCommand =
            "curl -X GET https://localhost:10100/district/"+fromBlock;
        getDistrict();
        getBlock();
    }

    void getDistrict() throws IOException {
        Reader reader = new InputStreamReader(
            Runtime.getRuntime().exec(districtCommand).getInputStream()
        );
        parseBlock(new JsonParser().parse(reader));
        fromBlock = currentBlock;
        districtCommand =
            "curl -X GET https://localhost:10100/district/"+fromBlock;
    }

    void getBlock() throws IOException {
        Reader reader = new InputStreamReader(
            Runtime.getRuntime().exec(blockCommand).getInputStream()
        );
        parseDistrict(new JsonParser().parse(reader));
        fromBlock = currentBlock;
        blockCommand =
            "curl -X GET https://localhost:10100/block/"+fromBlock;
    }

    void parseDistrict(JsonElement element){
        //do parsing stuff
    }

    void parseBlock(JsonElement element){
        //do parsing stuff
    }

}
