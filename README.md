# HLM
The High Level Manager (HLM), is a distributed system written in Java, with a client in C++, based on plain java remote object (with a limited set of types) that can be called directly by their name. It makes it extremely fast to set-up and use, so great for quick prototyping and tutorials or demonstrations.

It was used as part of the IPerG EU project, and we used it to successfully demonstrate a 3D/2D persistant multiplayer game, with 3D C++ clients and 2D mobile clients. The mobile clients were not directly connected to the main HLM, but to their server that uses a local HLM to communicate to the central one.
As the prototype was not extended afterward, the HLM was not updated much. It was used for an art exhibition, called See me, and a Student prototype.

As so, it is highly unsecure, the connections are always connected (i.e. all clients need to stay connected to their socket) and there are probably many other things to change or update to go toward something usable for serious applications. So if you do so, it is at your own risk.

# Usage and installation

TBC
