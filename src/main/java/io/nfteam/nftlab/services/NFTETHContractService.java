package io.nfteam.nftlab.services;

import io.nfteam.nftlab.contracts.NFTLabStoreEthereum;
import io.nfteam.nftlab.services.ipfs.IPFSService;
import io.nfteam.nftlab.services.ipfs.pinataresponses.Uploaded;
import io.nfteam.nftlab.services.smartcontract.*;
import org.springframework.core.io.ByteArrayResource;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.tx.gas.ContractGasProvider;

import java.math.BigInteger;
import java.util.List;

public class NFTETHContractService implements NFTContractService {
  private final IPFSService ipfsService;
  private final NFTLabStoreEthereum contractService;

  public NFTETHContractService(NFTLabStoreEthereum contractService, IPFSService ipfsService)
  {
    this.contractService = contractService;
    this.ipfsService = ipfsService;
  }

  public NFTID mint(UserTuple artist, ByteArrayResource file) throws Exception
  {
    Uploaded uploadedImage = ipfsService.upload(file);
    String hash = uploadedImage.getHash();
    String timestamp = uploadedImage.getTimestamp();

    NFTLabStoreEthereum.NFTLab newNft = new NFTLabStoreEthereum.NFTLab(artist.wallet(), artist.id(), hash, timestamp);

    contractService.mint(newNft).send();
    BigInteger tokenId = contractService.getTokenId(newNft.hash).send();

    return new NFTID(hash, tokenId);
  }

  public void transfer(
          BigInteger tokenId,
          UserTuple seller,
          UserTuple buyer,
          String price,
          String timestamp) throws Exception
  {
    NFTLabStoreEthereum.NFTTransaction transaction = new NFTLabStoreEthereum.NFTTransaction(tokenId, seller.wallet(), seller.id(), buyer.wallet(), buyer.id(), price, timestamp);

    contractService.transfer(transaction).send();
  }

  public List getHistory(BigInteger tokenId) throws Exception {
    return contractService.getHistory(tokenId).send();
  }

  public NFTLabStoreEthereum.NFTLab getNFTById(BigInteger tokenId) throws Exception {
    return this.contractService.getNFTById(tokenId).send();
  }

  public NFTLabStoreEthereum.NFTLab getNFTByHash(String hash) throws Exception {
    return this.contractService.getNFTByHash(hash).send();
  }

  public static NFTLabStoreEthereum deploy(
    Web3j web3j,
    Credentials credentials,
    ContractGasProvider contractGasProvider,
    String name,
    String symbol) throws Exception {
    return NFTLabStoreEthereum.deploy(
      web3j,
      credentials,
      contractGasProvider,
      name,
      symbol
    ).send();
  }

  public static NFTLabStoreEthereum load(
    String contractAddress,
    Web3j web3j,
    Credentials credentials,
    ContractGasProvider contractGasProvider) {
    return NFTLabStoreEthereum.load(
      contractAddress,
      web3j,
      credentials,
      contractGasProvider
    );
  }
}
