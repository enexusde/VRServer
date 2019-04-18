# VRServer
Virtual Reality Server (Java)

If you own a VR helmet, this allowes you to connect to a native VR client.

## Components
There are two components. 
1. The VR server (java) who serves the 3D models and Textures and everything.
2. The VR client (native 32bit) who is available on Steam to serve the Models.

The communication between those two components is done using a TCP connection (socket).

## Lifecycle

The folowing execution order must be respected.

1. You have to start the server (java).
2. You have to start the client (native x32).
3. The client asks for basic informations (like title of the native window).
4. The Server reports the title of the native window.
5. The client asks if any Models should be served by the server.
6. The server transports 3d Models and textures to the client.
7. The client show the Models having the textures.
8. You close the Client
9. You close the Server (optional).
