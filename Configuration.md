# Introduction #
Freedomotic android client and Fredomotic core must be configured to be able to communicate.

## Requirements ##
-Freedomotic must be running with an stomp endpoint available (see [http://code.google.com/p/freedomotic/wiki/ExamplePHP PHP example](.md)
-[RestApi](http://freedomotic.com/content/plugins/restapi) plugin must be installed on Freedomotic
-Freedomotic core must be running.

## Configuration ##
Once the Android Client is started, it must be configured to be able to connect with the Freedomotic core:
  1. Open the options menu / active bar menu items and select settings.
  1. Write the ip address of the server in which the core Freedomotic is running.
  1. Configure the port in wich the restapi is configured (8111 by default)
  1. Write the ip address where the broker is running (usually is the same as the freedomotic core)
  1. Write the port in which stomp is listening (usually the 61666)

After exit the settings screen, the client will retrieve the data from freedomotic. If something is wrong configured, the client will notify.