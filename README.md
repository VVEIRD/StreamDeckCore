# StreamDeckCore
This project provides api acces to any connected Elgato Stream Deck (Called ESD from now on). Windows, Linux and Mac OS X should be supported, but only windows could be tested. _This project is not associated in any way with Elgato Systems._

## Basic functionality
StreamDeckCore provides the following basic features:
1. Supporting multiple ESDs
2. Recognizing a connected ESD
3. Retriving all connected ESDs
4. Resetting the connected ESD
5 Setting the icons of the keys (0 - 14)
6. Setting the brightness of the ESD (0 - 99)
7. Recieving key pressed, released, clicked events from the ESD
8. Recieving events for key binds that are beeing displayed and when they are taken off through KeyEvents.
9. Custom animations for specific keys, at a 60/30/15 fps or custom fps.

## Advanced functionality
### StreamDeckController
The StreamDeckController class is an easy way to display content and create folder structures without the need of developing the code for folders etc. yourself.

### Soft Deck
If you have no Stream Deck yourself, but still want to develop for the plattform, you can use the software implementation to do that. Its a simple JFrame of all 15 "buttons" displayed. Its activated on default and can be disabled at the start of your program by setting de.rcblum.stream.deck.device.StreamDeckDevices.ENABLE_SOFTWARE_STREAM_DECK to false.

## Future functionality
?? ATM no new functionality planned

## Current objectives
1. Clean up sources
2. Document everything
3. Create tutorial & example programs

## Dependencies
### Github
This uses the github project https://github.com/nyholku/purejavahidapi (forked to https://github.com/WElRD/purejavahidapi).

__I'd recommend using the following fork for now, as the current version of nyholku's library introduces some errors that breaks this library: https://github.com/WElRD/purejavahidapi__. This version is also included in the releases.

### Maven
jna 4.0, gson and log4j, which can be downloaded through maven:

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
	<!-- https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-api -->
	<dependency>
		<groupId>org.apache.logging.log4j</groupId>
		<artifactId>log4j-api</artifactId>
		<version>2.9.0</version>
	</dependency>
	<!-- https://mvnrepository.com/artifact/org.apache.logging.log4j/log4j-core -->
	<dependency>
		<groupId>org.apache.logging.log4j</groupId>
		<artifactId>log4j-core</artifactId>
		<version>2.9.0</version>
	</dependency>
	
    

## Usage
For examples please see the [wiki](https://github.com/WElRD/StreamDeckCore/wiki)


