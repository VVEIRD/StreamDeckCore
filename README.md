# StreamDeckCore

[![Maven Central](https://img.shields.io/maven-central/v/io.github.vveird/StreamDeckCore.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22io.github.vveird%22%20AND%20a:%22StreamDeckCore%22)
[![SonarCloud Quality Gate](https://sonarcloud.io/api/project_badges/measure?project=VVEIRD_StreamDeckCore&metric=alert_status)](https://sonarcloud.io/dashboard?id=VVEIRD_StreamDeckCore)


This project provides api acces to any connected Elgato Stream Deck (Called ESD from now on). Windows, Linux and Mac OS X should be supported, but only windows could be tested. _This project is not associated in any way with Elgato Systems._

## Basic functionality
StreamDeckCore provides the following basic features:
1. Supporting multiple ESDs
2. Recognizing a connected ESD
3. Retriving all connected ESDs
4. Resetting the connected ESD
5. Setting the icons of the keys (0 - 14)
6. Setting the brightness of the ESD (0 - 99)
7. Recieving key pressed, released, clicked events from the ESD
8. Recieving KeyEvents if a key is beeing changed, one event for adding a key and one event for removing a key.
9. Custom animations for specific keys, at a 60/30/15 fps or custom fps.

## Advanced functionality
### StreamDeckController
The StreamDeckController class is an easy way to display content and create folder structures without the need of developing the code for folders etc. yourself.

### Soft Deck
If you have no Stream Deck yourself, but still want to develop for the plattform, you can use the software implementation to do that. Its a simple JFrame of all 15 "buttons" displayed. Its activated on default and can be disabled at the start of your program by using `de.rcblum.stream.deck.device.StreamDeckDevices.disableSoftwareStreamDeck();`

## Future functionality
?? ATM no new functionality planned

## Current objectives
1. Clean up sources
2. Document everything
3. Create tutorial & example programs

## Integration

### Maven

If you want to use this library in your maven project, just add the following dependency to you pom.xml:

    <dependency>
      <groupId>io.github.vveird</groupId>
      <artifactId>StreamDeckCore</artifactId>
      <version>1.0.2</version>
    </dependency>

### Github
This uses the github project https://github.com/nyholku/purejavahidapi (forked to https://github.com/WElRD/purejavahidapi).

__I'd recommend using the following fork for now, as the current version of nyholku's library introduces some errors that breaks this library: https://github.com/WElRD/purejavahidapi__. This version is also included in the releases.

The maven dependencies of the project reference the appropriate libaries.
	
    

## Usage
For examples please see the [wiki](https://github.com/WElRD/StreamDeckCore/wiki)


