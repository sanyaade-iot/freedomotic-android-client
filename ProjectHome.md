The first Android remote client to use with [Freedomotic](http://freedomotic.com/): a mashup oriented building and home automation framework.

### Android ICS screenshots: ###
<img src='http://freedomotic-android-client.googlecode.com/files/framed_FreedomoticClient_RoomsFragment.png' width='240' height='400'>
<img src='http://freedomotic-android-client.googlecode.com/files/framed_FreedomoticClient_ObjectDetail.png' width='240' height='400'>
<img src='http://freedomotic-android-client.googlecode.com/files/framed_FreedomoticClient_HousingPlanFragment.png' width='240' height='400'>
<h3>Tablet ScreenShots:</h3>
<img src='http://freedomotic-android-client.googlecode.com/files/framed_Tablet_FreedomoticClient_RoomsFragment.png' width='640' height='400'>
<img src='http://freedomotic-android-client.googlecode.com/files/framed_Tablet_FreedomoticClient_HousingPlanFragment.png' width='640' height='400'>

<h2>Steps to compile and Run the code</h2>

1) Clone the project from <a href='http://code.google.com/p/freedomotic-android-client/source/checkout'>Source</a>
2) Install and configure Android SDK, Eclipse and ADT. See <a href='http://developer.android.com/sdk/eclipse-adt.html#installing'>Installing the ADT plugin</a>

3) Load the cloned project in Eclipse.<br>
<br>
4) The library "freedomotic-data" must be referenced. This library is in the Freedomotic core package. The same library that is being used by the Freedomotic instance must be used here. In the lib folder is include a copy of the last version compiled. However if the freedomotic-data library is recompiled, the new library must be used here<br>
<br>
5) The library "restapiinterfaces must be referenced. This library is in the Freedomotic tools package. It must be used the same way the freedomotic-data<br>
<br>
6) This proyect now uses the great android libraries <a href='http://actionbarsherlock.com/'>ActionBarSherlock </a> and <a href='http://viewpagerindicator.com/'>ViewPagerIndicator</a>. The source code of that libraries is include on the /lib path. This projects relies on actionbarsherlock 4.0  version. To be able to compile, both projects must be included as an android library . Please refer to their installation and configuration instructions available in their sites.<br>
<br>
Althought the compilation Library is Android 4.0.3, the code should run in at least Android >= 2.2 devices.<br>
<br>
<a href='http://code.google.com/p/freedomotic-android-client/wiki/Configuration'>How to configure</a>