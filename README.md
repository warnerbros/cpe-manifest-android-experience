# Development Guide #
## Workspace ##
1. Download Android Studio
2. Check out NextGen Android at this repository, e.g. NextGen/Android folder
## How to build with Grade ##
### Set Up ###
1. Install Gradle  
	- Download Gradle version 1.10 at http://www.gradle.org/downloads  
	- Add `GRADLE_HOME/bin` to your PATH environment variable.   
	- Run `gradle -v` at command line to test installation.
2. Install Android SDK Build Tools, Android Support Library and Android Support Repository  
	- Start the Android SDK Manager.
	- In the SDK Manager window, find the Tools folder, install Android SDK Build-tools 19
	- In the SDK Manager window, scroll to the end of the Packages list, find the Extras folder, install Android Support Library and Android Support Repository.
3. (Optional) Install Gradle IDE Pack plugin in Eclipse
   Go to Eclipse Market Place and search for Gradle. Install Gradle IDE Pack
4. Check out library projects and place them under the same directory as this project
5. In the same directory, create settings.gradle and build.gradle

	settings.gradle:

		include 'Android', 'flixster-android-closedcaptions', 'StickyGridHeaders:Library', 'google-play-services_lib'

	build.gradle:

		buildscript {
			repositories {
				mavenCentral()
			}
			dependencies {
				classpath 'com.android.tools.build:gradle:0.9.+'
			}
		}

6. Add build.gradle in google-play-services_lib folder

	build.gradle:

		buildscript {
				repositories {
				mavenCentral()
		}
		
		dependencies {
				classpath 'com.android.tools.build:gradle:0.9.2'
			}
		}

		apply plugin: 'android-library'

		dependencies {
			compile files('libs/google-play-services.jar')
		}

		android {
			compileSdkVersion 19
			buildToolsVersion "19"

			defaultConfig {
				minSdkVersion 10
				targetSdkVersion 19
			}

			sourceSets {
				main {
					manifest.srcFile 'AndroidManifest.xml'
					java.srcDirs = ['src']
					res.srcDirs = ['res']
				}
			}
		}

### Build ###
#### Build Crowdin ####
1. On a command line, cd into Android project
2. Type `gradle crowdin`


#### Build Main APK ####
1. On a command line, cd into Android project
2. Type `gradle clean assemble` 
3. If you are making the RC build, sign the RC apk with

		jarsigner -verbose -sigalg MD5withRSA -digestalg SHA1 -keystore ./release/jillmoto_android_key ./build/apk/Android-rc-unsigned.apk jillmoto

4. Verify and zip align the apk  

		jarsigner -verify -verbose ./build/apk/Android-rc-unsigned.apk  
		zipalign -v 4 ./build/apk/Android-rc-unsigned.apk ./build/apk/Android-rc-signed.apk