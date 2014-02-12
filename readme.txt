WORK FLOW:
Each process starts from messagePasser, read the config file, fill the user Map, rule list, start a listener to listen connection requests from other nodes, start another listener to listen user input
For connection listener, start a Pairlistener thread to dedicately listen the input from this socket.

For user input, currently we can read keyboard input. But actually we can add some well-format auto input at first,
So, whenever user input a "send", we call send() function. In send function, we match rule first and do something different.
If we just need to send away, we need to check the connection has been built. If not, create a connection, and add inputsrteam into a pool.

PairlistenTread matches receiveRules and handle it just like what send do.

TODO major:
1. handle and match different rules
2. delay queue, input queue.
3. auto test
.... a lot
4. solve duplicate

TODO min:
1. missing/wrong value check
2. set kind as enum
3. close connection or input sometimes?
4. makefile run (input file location needs to be changed)
