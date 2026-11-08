# Documentation Overview
### Neuronal Representation
Each neuronal element (receptor, dendrite, soma, axon, synapse, effector, feedback) is explicitly represented.  
One could build a strongly simplified model of the neuron, as is done in LLMs.  
LLMs reduce so drastically that only edges with data exist, and the nodes are merely mathematical functions.

In my model, I do not make this reduction. This has advantages, but also disadvantages.  
A disadvantage would be memory consumption.  
The advantage is that each neuronal element has specific tasks.  
The dendrite, unlike conventional concepts, decides based solely on a weight about how data is transmitted to the soma.  
The dendrite balances itself based on feedback and the transmission of the potential time point.  
The soma sums the dendrite values until a threshold is reached. The inputs from the dendrites are used only for threshold calculation.  
The data within the soma is determined solely by LTP and STP. These values are updated with each stimulus.  
When the soma fires, LTP and STP are proportionally passed to the axon based on their potentials.  
The axon is essentially a broadcast messenger that transmits data to all synapses through inhibitory and excitatory influences.  
The synapse can also be individually modulated by inhibitory and excitatory influences. Otherwise, the stimulus leaves the neuron here.

### Continuous Learning Coupling
I view the learning process as the interaction of the SNN with the environment. 
There is no super-intelligence that controls the learning success. If the SNN wants to take over control of the room temperature, 
the temperature sensor knows the value and the temperature regulator knows how far the actual temperature has deviated from the target temperature. 
This value is transmitted as a delta value to the SNN. Before that, the value is converted into a potential value. 
This makes it unnecessary to divide the SNN into different phases. The learning process continuously takes place in interaction with the environment.

### Self-Regulation
The SNN must self-regulate. This happens by each neuron element controlling its own parameters. The parameters are defined by the clock, the stimuli, and two feedbacks, namely Time-Feedback and Value-Feedback. 
Unlike conventional approaches, the weight in my SNN is anchored in the dendrite. The weight is adjusted based on "when I passed on the potential" and "what was the Value-Feedback associated with it." These two events must correlate within a certain timeframe for the weight to change. The weight is placed within a range between 0 and 1 using a mathematical function. 
When the weight reaches the soma, the values are summed there. Meanwhile, the potential may also time-governed return to a resting value. Firing occurs only if the threshold is reached and the refractory period has elapsed. 
With each stimulus and feedback, the potential, threshold, LTP, and SPT can change. Here as well, "everything can return to a resting value" applies. 
Threshold changes can only be influenced by Time-Feedbacks. The time behavior of the neuron is determined by the threshold. Here too, mathematical functions ensure homeostasis. 
In addition to the decline, potentials also increase in LTP and SPT. With each firing, the values are reduced by their share of the action potential. 
The axon remains passive and does not model any active potentials. Similarly, the synapse behaves analogously.
Aktionspotential verringert. Das Axon bleibt passiv und modelliert keine aktiven Potentiale. Analog bei der Synapse.

### Asynchronous Communication
The stimuli are processed asynchronously. 
A critical point is to ensure that there is no stimulus explosion. The axon would act like a multiplier of stimuli. 
Therefore, I decided not to use an event-based architecture. The stimuli flow, as in the biological sense, through the axons, resulting in a fixed number of data flowing through the network. 
However, the axon will continue to fire the synapses through these 'paths' with stimuli, which could lead to congestion. 
To this end, there is a mini buffer in the 'Point to Point' transmission. If the buffer is full, the values can be summed. When summing, it is important to ensure they do not exceed the minimum and maximum values. 
With the drop to a resting value, the data are not further distributed. In particular, there is a stop of the stimulus flow when the soma does not fire. 
Another measure is to assign a TTL to the stimuli so they do not circulate indefinitely. A thread will execute the processing starting from the axon through the soma to the next axon. 
It has been shown that virtual threads must be kept busy; otherwise, a single thread would process everything sequentially. 
In principle, a thread runs from Axon->Dendrite->Soma and, depending on the firing event, continues to the next axon. 
The axon enters the values to be transmitted into a scheduler list for a subsequent thread.

### Agent as Mediator
As the environment in the SNN is the tutor, a real reinforcement learning agent is not needed. 
The agent's only task is to act as a language translator. From a sensor reading of 'Too hot', it must generate a potential between -70mV and +70mV. It performs no evaluation and executes the transformations bidirectionally. In particular, there is no generic agent. The base classes in the Java SNN are provided.


### Environment as Learning Unit
As already mentioned, the environment is responsible for learning and works together with the agent. 
Therefore, the environment must be able to fulfill this task. Here, the time behavior of the SNN needs to be checked, as well as the values of the SNN's effectors. In all cases, the environment sends a delta value. 
A "too high" signal from a sensor is then approximately "-20". The data are sent with a sign to show the SNN which direction it needs to take. 
In principle, this also happens in biology. When considering the skin as a sensor, pressure is transmitted from gentle to pain to the brain. 
The time behavior is also a learning step. A pain reaction must happen promptly. Especially with the Hebbian learning rule, which is implemented in the dendrite, the feedback must be transmitted promptly. 
Analogous to the description of the axon as a multiplexer, the environment also has a similar behavior. 
It can send a matrix of data. Here, the same process applies as with the axon, but without data modulation.

### Performance & Scalability
With the new Java FFM features, it is now possible to access external storage and avoid garbage collection. 
The application therefore focuses on data-driven design with maximum performance. Since foreign tools usually do not use this, one has to completely give up on them. 
The focus on maximum performance leads to everything being statically organized at runtime. Only necessary objects are set up as singletons. 
I have currently checked the option of whether the GPU can be used. Although it can be extremely fast, the data input is not performant. Putting the entire network into the graphics card is often not an option due to low memory capacity. 
A simple test with Java's MemorySegment showed me isolated in the test an access time in the nanoseconds range. Comparing that with a typical database, it is up to a million times faster. More complex structures lie in the microseconds range during reading and writing. The behavior in the running SNN is not yet available to me. The basis for high performance and large memory is given. In particular, one can check whether to connect the MemorySegment with the SSD storage. 
Although the performance appears sufficiently good, one must check whether it is even needed. In biology, signals are typically transmitted in the millisecond range. This will still need to be investigated.
 
## **Event & Processing Architecture**
As already described, the stimuli are not placed into a queue and processed by worker threads. Instead, the network is used as a transmission medium. The edges of the system are the interfaces from the receptor to the SNN and internally from the axon to other neurons. These two points would generate many stimuli. An internal transfer, such as from dendrite to soma, is executed directly.
The Reinforcement Learning agent transfers potentials to the receptors. He does this by filling the scheduler with data. Once the data is written, he marks the dataset as released for transmission. A so-called worker thread can now execute the transmission.
This scheduler structure is stored in the off-heap of the system and contains the source and destination along with the corresponding potential. Due to a possible explosion of stimuli, the values are stored in a ring buffer per source-target pair. This ring buffer should be very small. In the current version, there are 3 float values. These 3 values are then each for the stimulus and the two feedbacks (Time, Value).
The axon behaves analogously to the receptor.

 