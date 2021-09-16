package etherlandscore.etherlandscore.eth;

import org.bukkit.Bukkit;
import org.web3j.crypto.Keys;
import org.web3j.crypto.Sign;
import org.web3j.utils.Numeric;

import java.security.SignatureException;

public class LinkInformation {
  static final String MESSAGE_PREFIX = "\u0019Ethereum Signed Message:\n";
  String message;
  String signature;
  String publickey;
  boolean passed = false;

  public LinkInformation(String message, String signature, String publickey) {
    this.message = message;
    this.signature = signature;
    this.publickey = publickey;
  }

  static byte[] getEthereumMessagePrefix(int messageLength) {
    return MESSAGE_PREFIX.concat(String.valueOf(messageLength)).getBytes();
  }

  public boolean didPass() {
    return passed;
  }

  private byte[] getEthereumMessageHash(byte[] message) {
    byte[] prefix = getEthereumMessagePrefix(message.length);
    byte[] result = new byte[prefix.length + message.length];
    System.arraycopy(prefix, 0, result, 0, prefix.length);
    System.arraycopy(message, 0, result, prefix.length, message.length);
    return result;
  }

  public String getPubkey() {
    return publickey;
  }

  public void pubkey() {
    String r = signature.substring(0, 66);
    String s = signature.substring(66, 130);
    String v = "0x" + signature.substring(130, 132);
    byte[] msgBytes = getEthereumMessageHash(message.getBytes());
    String pubkey;
    try {
      pubkey = Sign.signedMessageToKey(
              msgBytes,
              new Sign.SignatureData(
                  Numeric.hexStringToByteArray(v)[0],
                  Numeric.hexStringToByteArray(r),
                  Numeric.hexStringToByteArray(s)))
          .toString(16);
    } catch (SignatureException e) {
      return;
    }
    String recoveredAddress = "0x" + Keys.getAddress(pubkey);
    Bukkit.getLogger().info("public key: " + recoveredAddress + " : " + this.message);
    if (recoveredAddress.equalsIgnoreCase(this.publickey)) {
      this.publickey = recoveredAddress.toLowerCase();
      this.passed = true;
    }
  }

  public String uuid() {
    return message.split("_")[0];
  }
}
