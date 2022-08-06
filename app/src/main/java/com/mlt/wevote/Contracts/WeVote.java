package com.mlt.wevote.Contracts;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple3;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.4.1.
 */
@SuppressWarnings("rawtypes")
public class WeVote extends Contract {
    public static final String BINARY = "60806040526000600355600060045560016005553480156200002057600080fd5b5060405162000d1c38038062000d1c8339810160408190526200004391620001f2565b600080546001600160a01b0319163317815563ffffffff808416600155821660025583516003555b600354811015620000df578381815181106200008b576200008b62000363565b602090810291909101810151600083815260068352604090208151620000b793919290910190620000e9565b5060008181526006602052604081206001015580620000d68162000379565b9150506200006b565b50505050620003e0565b828054620000f790620003a3565b90600052602060002090601f0160209004810192826200011b576000855562000166565b82601f106200013657805160ff191683800117855562000166565b8280016001018555821562000166579182015b828111156200016657825182559160200191906001019062000149565b506200017492915062000178565b5090565b5b8082111562000174576000815560010162000179565b634e487b7160e01b600052604160045260246000fd5b604051601f8201601f191681016001600160401b0381118282101715620001d057620001d06200018f565b604052919050565b805163ffffffff81168114620001ed57600080fd5b919050565b6000806000606084860312156200020857600080fd5b83516001600160401b03808211156200022057600080fd5b818601915086601f8301126200023557600080fd5b81516020828211156200024c576200024c6200018f565b8160051b6200025d828201620001a5565b928352848101820192828101908b8511156200027857600080fd5b83870192505b848310156200033457825186811115620002985760008081fd5b8701603f81018d13620002ab5760008081fd5b8481015187811115620002c257620002c26200018f565b620002d6601f8201601f19168701620001a5565b81815260408f81848601011115620002ee5760008081fd5b60005b838110156200030e578481018201518382018a01528801620002f1565b83811115620003205760008985850101525b50508452505091830191908301906200027e565b985062000346915050888201620001d8565b9550505050506200035a60408501620001d8565b90509250925092565b634e487b7160e01b600052603260045260246000fd5b60006000198214156200039c57634e487b7160e01b600052601160045260246000fd5b5060010190565b600181811c90821680620003b857607f821691505b60208210811415620003da57634e487b7160e01b600052602260045260246000fd5b50919050565b61092c80620003f06000396000f3fe608060405234801561001057600080fd5b50600436106100575760003560e01c8063069953a71461005c57806314a0069a146100895780638e7ea5b2146100a957806397545f29146100c0578063e7d0759b146100d7575b600080fd5b61006f61006a36600461065e565b6100ec565b604080519283526020830191909152015b60405180910390f35b61009c61009736600461065e565b6101bd565b60405161008091906106f3565b6100b1610286565b6040516100809392919061070d565b6100c960025481565b604051908152602001610080565b6100ea6100e5366004610732565b61039f565b005b6000805481906001600160a01b0316331461010657600080fd5b600254421161011457600080fd5b60005b6003548110156101b1578360405160200161013291906107ba565b60408051601f198184030181528282528051602091820120600085815260068352929092209192610164929101610811565b60405160208183030381529060405280519060200120141561019f576000908152600660205260409020600101546004549092509050915091565b806101a9816108c3565b915050610117565b50600093849350915050565b6000546060906001600160a01b031633146101d757600080fd5b6007826040516101e791906107ba565b9081526040519081900360200190208054610201906107d6565b80601f016020809104026020016040519081016040528092919081815260200182805461022d906107d6565b801561027a5780601f1061024f5761010080835404028352916020019161027a565b820191906000526020600020905b81548152906001019060200180831161025d57829003601f168201915b50505050509050919050565b600080546060919081906001600160a01b031633146102a457600080fd5b60025442116102b257600080fd5b6000606060005b6003548110156103905760008181526006602052604090206001015483101561037e576000818152600660205260409020600181015481549094506102fd906107d6565b80601f0160208091040260200160405190810160405280929190818152602001828054610329906107d6565b80156103765780601f1061034b57610100808354040283529160200191610376565b820191906000526020600020905b81548152906001019060200180831161035957829003601f168201915b505050505091505b80610388816108c3565b9150506102b9565b50600454909591945092509050565b6000546001600160a01b031633146103b657600080fd5b60015442106103c457600080fd5b60408051600081526020810191829052518120906007906103e69086906107ba565b9081526040516020918190038201812061040292909101610811565b604051602081830303815290604052805190602001201461042257600080fd5b60005b60035481101561051c578160405160200161044091906107ba565b60408051601f198184030181528282528051602091820120600085815260068352929092209192610472929101610811565b60405160208183030381529060405280519060200120141561050a57600554600082815260066020526040812060010180549091906104b29084906108de565b9091555050600554600480546000906104cc9084906108de565b92505081905550826007856040516104e491906107ba565b90815260200160405180910390206000019080519060200190610508929190610522565b505b80610514816108c3565b915050610425565b50505050565b82805461052e906107d6565b90600052602060002090601f0160209004810192826105505760008555610596565b82601f1061056957805160ff1916838001178555610596565b82800160010185558215610596579182015b8281111561059657825182559160200191906001019061057b565b506105a29291506105a6565b5090565b5b808211156105a257600081556001016105a7565b634e487b7160e01b600052604160045260246000fd5b600082601f8301126105e257600080fd5b813567ffffffffffffffff808211156105fd576105fd6105bb565b604051601f8301601f19908116603f01168101908282118183101715610625576106256105bb565b8160405283815286602085880101111561063e57600080fd5b836020870160208301376000602085830101528094505050505092915050565b60006020828403121561067057600080fd5b813567ffffffffffffffff81111561068757600080fd5b610693848285016105d1565b949350505050565b60005b838110156106b657818101518382015260200161069e565b8381111561051c5750506000910152565b600081518084526106df81602086016020860161069b565b601f01601f19169290920160200192915050565b60208152600061070660208301846106c7565b9392505050565b60608152600061072060608301866106c7565b60208301949094525060400152919050565b60008060006060848603121561074757600080fd5b833567ffffffffffffffff8082111561075f57600080fd5b61076b878388016105d1565b9450602086013591508082111561078157600080fd5b61078d878388016105d1565b935060408601359150808211156107a357600080fd5b506107b0868287016105d1565b9150509250925092565b600082516107cc81846020870161069b565b9190910192915050565b600181811c908216806107ea57607f821691505b6020821081141561080b57634e487b7160e01b600052602260045260246000fd5b50919050565b600080835481600182811c91508083168061082d57607f831692505b602080841082141561084d57634e487b7160e01b86526022600452602486fd5b81801561086157600181146108725761089f565b60ff1986168952848901965061089f565b60008a81526020902060005b868110156108975781548b82015290850190830161087e565b505084890196505b509498975050505050505050565b634e487b7160e01b600052601160045260246000fd5b60006000198214156108d7576108d76108ad565b5060010190565b600082198211156108f1576108f16108ad565b50019056fea26469706673582212207a501f82a101d38b8ef14034c76fbef5f684ebbfc6d30de3f735255ecc652c3f64736f6c63430008090033";

    public static final String FUNC_GETVOTECOUNT = "getVoteCount";

    public static final String FUNC_GETVOTER = "getVoter";

    public static final String FUNC_GETWINNER = "getWinner";

    public static final String FUNC_RESULTDATE = "resultDate";

    public static final String FUNC_WRITEVOTE = "writeVote";

    @Deprecated
    protected WeVote(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected WeVote(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected WeVote(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected WeVote(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<Tuple2<BigInteger, BigInteger>> getVoteCount(String _candidateId) {
        final Function function = new Function(FUNC_GETVOTECOUNT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_candidateId)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple2<BigInteger, BigInteger>>(function,
                new Callable<Tuple2<BigInteger, BigInteger>>() {
                    @Override
                    public Tuple2<BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue());
                    }
                });
    }

    public RemoteFunctionCall<String> getVoter(String epicNumber) {
        final Function function = new Function(FUNC_GETVOTER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(epicNumber)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Tuple3<String, BigInteger, BigInteger>> getWinner() {
        final Function function = new Function(FUNC_GETWINNER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple3<String, BigInteger, BigInteger>>(function,
                new Callable<Tuple3<String, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple3<String, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple3<String, BigInteger, BigInteger>(
                                (String) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue());
                    }
                });
    }

    public RemoteFunctionCall<BigInteger> resultDate() {
        final Function function = new Function(FUNC_RESULTDATE, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> writeVote(String epicNumber, String _voter, String _candidateId) {
        final Function function = new Function(
                FUNC_WRITEVOTE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(epicNumber), 
                new org.web3j.abi.datatypes.Utf8String(_voter), 
                new org.web3j.abi.datatypes.Utf8String(_candidateId)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static WeVote load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new WeVote(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static WeVote load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new WeVote(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static WeVote load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new WeVote(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static WeVote load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new WeVote(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<WeVote> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, List<String> _candidates, BigInteger _voteDate, BigInteger _resultDate) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Utf8String>(
                        org.web3j.abi.datatypes.Utf8String.class,
                        org.web3j.abi.Utils.typeMap(_candidates, org.web3j.abi.datatypes.Utf8String.class)), 
                new org.web3j.abi.datatypes.generated.Uint32(_voteDate), 
                new org.web3j.abi.datatypes.generated.Uint32(_resultDate)));
        return deployRemoteCall(WeVote.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<WeVote> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, List<String> _candidates, BigInteger _voteDate, BigInteger _resultDate) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Utf8String>(
                        org.web3j.abi.datatypes.Utf8String.class,
                        org.web3j.abi.Utils.typeMap(_candidates, org.web3j.abi.datatypes.Utf8String.class)), 
                new org.web3j.abi.datatypes.generated.Uint32(_voteDate), 
                new org.web3j.abi.datatypes.generated.Uint32(_resultDate)));
        return deployRemoteCall(WeVote.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<WeVote> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, List<String> _candidates, BigInteger _voteDate, BigInteger _resultDate) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Utf8String>(
                        org.web3j.abi.datatypes.Utf8String.class,
                        org.web3j.abi.Utils.typeMap(_candidates, org.web3j.abi.datatypes.Utf8String.class)), 
                new org.web3j.abi.datatypes.generated.Uint32(_voteDate), 
                new org.web3j.abi.datatypes.generated.Uint32(_resultDate)));
        return deployRemoteCall(WeVote.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<WeVote> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, List<String> _candidates, BigInteger _voteDate, BigInteger _resultDate) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Utf8String>(
                        org.web3j.abi.datatypes.Utf8String.class,
                        org.web3j.abi.Utils.typeMap(_candidates, org.web3j.abi.datatypes.Utf8String.class)), 
                new org.web3j.abi.datatypes.generated.Uint32(_voteDate), 
                new org.web3j.abi.datatypes.generated.Uint32(_resultDate)));
        return deployRemoteCall(WeVote.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }
}
