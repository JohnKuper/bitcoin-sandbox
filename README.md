# Preface 
After completing the course "Bitcoin and Cryptocurrency Technologies" by Princeton University and reading the book "Mastering Bitcoin: Programming the Open Blockchain" I decided to practice a little bit with new material.

I did not want to code one more boring GitHub REST API client and showing list of repositories on the screen. Instead, I wanted to implement something interesting with business logic under the hood.

UI is as simple as possible since it's not a goal here and very time-consuming.

# Bitcoin sandbox
Bitcoin sandbox is a simplified version of Bitcoin network fundamentals like:

- Identities or users (key pairs and special bitcoin addresses)
- Transaction, TransactionInput, TransactionOutput, Unspent transaction output (UTXO)
- Transaction signing and validating
- Merkle root
- Block mining, considering network difficulty
- Connecting blocks together into the blockchain
    
# How everything works:
1. At the beginning network should be bootstrapped, therefore 3 new users Satoshi, Alice, and Bob are created automatically. Each of them has a unique key pair. Satoshi is set as an active user and he is the author of a genesis block. More users can be created if needed.
2. Users are able to send coins to each other.
3. After each transfer UTXO pool is updated accordingly. (i.e sender loses its UTXOs, receiver acquires UTXOs and can use them in the future).
4. All transactions are collected into the so-called Mempool. Miner takes transactions from this pool and uses them while mining the next block .
5. Finally, once block is minted it's added to the blockchain with all necessary information inside it.
6. Everything is cached into the database.

# Used technologies:
- Kotlin
- Architecture components (Room, LiveData, ViewModel)
- MVVM
- Dagger 2
- RxJava 2
- Cryptography

# Used materials:
- https://coursera.org/learn/cryptocurrency
- https://www.amazon.com/Mastering-Bitcoin-Programming-Open-Blockchain-ebook/dp/B071K7FCD4
- https://en.bitcoin.it/wiki
