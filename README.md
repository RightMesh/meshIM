# meshIM

meshIM is an on- or off-line Android messaging application built on [the RightMesh library](https://rightmesh.io).

You can send messages to other meshIM users over the internet, or form automatically form a local network to send messages to devices nearby!

## RightMesh

RightMesh is a software-based, mobile mesh networking platform and protocol using blockchain technology and tokens to enhance global connectivity. Find out more at [RightMesh.io](https://rightmesh.io)

## Building meshIM

You can clone this repository and build meshIM in Android Studio. You will need your own RightMesh developer credentials, license key, and mesh port to build and run the application. Check out the [RightMesh DeveloperPortal](https://developer.rightmesh.io) to sign up and find instructions.

The meshIM key is as follows and may be put in the global properties folder located in $HOME/.gradle/gradle.properties
>> rightmesh_meshim_key="0x4282c537813c21fcfb59860a89c94546564635f5"

## Dependencies

Aside from RightMesh, meshIM uses [Gson](https://github.com/google/gson) for JSON serialization and Room for database abstraction, as well as some other Google support libraries for backwards compatibility.
