# Development Guide #
## Workspace ##
1. Download Android Studio
2. Check out cpe-manifest-android-experience at this repository, e.g. cpe-manifest-android-experience folder

## How to build with Grade ##
### Set Up ###
1.  a.  Place cpe-manifest-android-experience under the same directory as your project
        In the same directory, modify your settings.gradle

	    settings.gradle:

		    include ':cpe-manifest-android-experience', ...

    b.  Or if you want to check out the project into other folder not the same directory as your project:
        settings.gradle:

            include ':cpe-manifest-android-experience', ...
            project(":cpe-manifest-android-experience").projectDir = file("**Relative Path of cpe-manifest-android-experience**")


3. Add cpe-manifest-android-experience into the dependencies of build.gradle file of your project

	build.gradle:

        ...
		dependencies {
            ...
            compile project(':cpe-manifest-android-experience')
            ...
        }
        ...
