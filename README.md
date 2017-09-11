# StreamDeckCore
A java implementation for the Elgato Stream Deck (Called ESD from now on). Windows Linux and Mac OS X should be supported, albeit only Windows was tested.

This is in a very early development state, so it is bound to change a lot.

## Basic functionality
StreamDeckCore provides the following features as of now:
1. Recognizing a connected ESD
2. Resetting the connected ESD
3. Setting the icons of the keys (0 - 14)
4. Setting the brightness of the ESD (0 - 99)
5. Recieving key pressed, released, clicked events from the ESD
6. Recieving events for key binds that are beeing displayed and when they are taken off (onDisplay() and offDisplay())
7. Animation on pressed key
8. Custom animations for specific keys, at a decent framrate (60/30/15 fps) if possible.
9. Supporting multiple ESDs

## Future functionality
?? ATM no new functionality planned

## Current objectives
1. Clean up sources
2. Document everything
3. Create tutorial & example programs

## Dependencies
This uses the github project https://github.com/nyholku/purejavahidapi (forked to https://github.com/WElRD/purejavahidapi), jna 4.0 and gson, which can be downloaded through maven:

    <!-- https://mvnrepository.com/artifact/net.java.dev.jna/jna -->
    <dependency>
        <groupId>net.java.dev.jna</groupId>
        <artifactId>jna</artifactId>
        <version>4.0.0</version>
    </dependency>
    <!-- https://mvnrepository.com/artifact/com.google.code.gson/gson -->
	<dependency>
	    <groupId>com.google.code.gson</groupId>
	    <artifactId>gson</artifactId>
	    <version>2.8.1</version>
	</dependency>
    

## Usage
For examples please see the [wiki](https://github.com/WElRD/StreamDeckCore/wiki)


