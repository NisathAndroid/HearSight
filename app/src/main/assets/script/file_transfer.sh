#!/bin/sh

shareFilePath="$1"
realPath="/sdcard/Download/HearSightAudio/GalleryAudios/IMG-20231029-WA0010.mp3"
destinationPath="/home/sunil/"

adb pull "$realPath" "$destinationPath"
