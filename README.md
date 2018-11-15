# meshIM

meshIM is an on- or off-line Android messaging application built on [the RightMesh library](https://rightmesh.io).

You can send messages to other meshIM users over the internet, or form automatically form a local network to send messages to devices nearby!

## RightMesh

RightMesh is a software-based, mobile mesh networking platform and protocol using blockchain technology and tokens to enhance global connectivity. Find out more at [RightMesh.io](https://rightmesh.io)

## Building meshIM

You can clone this repository and build meshIM in Android Studio. You will need your own RightMesh developer credentials, license key, and mesh port to build and run the application. Check out the [RightMesh DeveloperPortal](https://developer.rightmesh.io) to sign up and find instructions.

The official meshIM key is as follows
>> rightmesh_meshim_key="0x4282c537813c21fcfb59860a89c94546564635f5" 

This key makes use of the official meshIM Mesh Ports and can only be used by approved developers. You may swap the key out for your own to build meshIM, however you will not be able to communicate with users running an official meshIM build.

## Dependencies

Aside from RightMesh, meshIM uses [Gson](https://github.com/google/gson) for JSON serialization and Room for database abstraction, as well as some other Google support libraries for backwards compatibility.
