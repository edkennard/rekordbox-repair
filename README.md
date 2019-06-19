## rekordbox Repair

### Introduction
rekordbox Repair is designed to help users of Pioneer's rekordbox DJ software fix problems with their rekordbox collections, as well as keep them clean on an ongoing basis. I wrote it as part of some consulting work helping George Evelyn a.k.a. [Nightmares on Wax](nightmaresonwax.com) sort out his studio and DJ'ing computers. George has a MacOS rekordbox collection of around 7200 tracks and counting, so it's against that that this tool has initially been battle-tested.

rekordbox Repair is a simple command line tool which works on both MacOS and Windows. It analyses an exported rekordbox collection in XML format, searches the folder on disk where your music files live, then produces the following two files:

1. **A new rekordbox XML file** containing the repaired tracks with their correct file locations, original cue points and loops, and the playlists they belong to. This XML file can be loaded into rekordbox via its "rekordbox xml" area, and can be generated in two ways depending on how you want to repair your collection and playlists:
    - **Selectively repair tracks in your existing collection and playlists** - A new XML file is generated containing only the tracks the tool was able to repair, and the playlists they belong to.
    - **Rebuild your entire collection and all playlists** - A new XML file is generated containing your entire collection, including the tracks which the tool was able to repair.  This option is useful if there's a large number of repaired tracks spread over many playlists which would be time-consuming to fix using the first option above. You could also use this option if you've moved your entire music library to another location on your disk, or to another disk, and you need to update your rekordbox collection without losing precious information such as cue points and loops.
2. **A detailed report** listing the following issues the tool has detected:
    - Tracks which rekordbox is reporting as "File missing", but which have really just moved location on the disk and can therefore be repaired.
    - Tracks which genuinely do have their file missing, and should therefore be removed from rekordbox.
    - Files on disk which can't be imported to rekordbox because the full path to their location exceeds rekordbox's 255 character limit (only applies to MacOS)
    - Files on disk which haven't been imported into rekordbox yet.

### Can't rekordbox fix missing files itself?
rekordbox does have a 'Relocate' tool for repairing tracks with missing files, but it only works for individual tracks one-by-one or, at best, a set of tracks with their files located within the same single folder (e.g. for an album). Since you have to manually tell rekordbox where each missing file is on the disk, if you have 100s of tracks with missing files, that's going to take more patience than any reasonable person has.

Other products such as Traktor and Serato have better tools for this, where you can specify the root folder of your music then missing files will be searched for within that folder and all of its subfolders. Hopefully Pioneer will do something similar in the future, and make this tool redundant.   

### How do tracks in rekordbox end up with missing files?
There are various reasons why rekordbox might have tracks with missing files:
- If you've manually moved some music files or their folders around on your disk using Finder/Windows Explorer.
- If you use a music library manager such as iTunes or Swinsian and have the "Keep library folder organised" option switched on. Whenever you edit a track's artist or album name, e.g. from "Incorrect Artist" to "Correct Artist" and from "Incorrect Album" to "Correct Album", the related file will be moved from the folder /Incorrect Artist/Incorrect Album to a new folder /Correct Artist/Correct Album, breaking rekordbox's reference to the file.
- If you imported tracks directly from an external device (e.g. an external drive or USB stick), but that device isn't connected anymore.
- The files have genuinely been deleted from your disk.

### rekordbox Repair in action
To demonstrate how this tool can be used to clean up a rekordbox collection, here is a working example with a deliberately broken collection and the complete sequence of steps that were taken to fix all the various issues.

**Important note: Always make sure you have a complete and functioning backup of your computer before doing any work on your rekordbox collection.**

#### Demo broken rekordbox collection
![rekordbox collection with missing files](/images/01_missing_files.png?raw=true "Collection with missing files")

#### Demo broken rekordbox playlist
![rekordbox collection with missing files](/images/02_missing_files_in_playlist.png?raw=true "Playlist with missing files") 

#### Step 1: Exported collection and playlists to XML
![Export collection](/images/00_export_collection.png?raw=true "Export collection")

#### Step 2: Ran rekordbox Repair against exported XML and iTunes music folder
```bash
VividLab-Mac-Pro:rekordbox-repair-0.1 Ed$ bin/rekordbox-repair -i "/Users/Ed/Documents/rekordbox/original-library.xml" -o "/Users/Ed/Documents/rekordbox/fixed-library.xml" -s "/Users/Ed/Music/rekordbox iTunes/iTunes Media/Music"
``` 

#### rekordbox Repair's output
```text
2019-06-17 12:51:02,476 INFO  - Running using Java version 1.8.0_211
2019-06-17 12:51:02,890 INFO  - Analysing rekordbox collection...
2019-06-17 12:51:02,890 INFO  - Reading rekordbox XML file /Users/Ed/Documents/rekordbox/original-library.xml...
2019-06-17 12:51:02,964 INFO  - Loading tracks in the collection...
2019-06-17 12:51:02,983 INFO  - Checking playlists...
2019-06-17 12:51:02,989 INFO  - Locating files referenced by tracks in the collection...
2019-06-17 12:51:02,991 INFO  - 'Nalin & Kane' - 'Essential Selection '97 - Winter - Pete Tong' - 'Beachball' - OK
2019-06-17 12:51:02,991 INFO  - 'Solu Music' - 'Fade feat. KimBlee' - 'Fade feat. KimBlee (Original Mix (Part I))' - OK
2019-06-17 12:51:02,991 INFO  - 'Way Out West' - 'Lullaby Horizon (Ben Bohmer Remix)' - 'Lullaby Horizon (Ben Bohmer Extended Mix)' - OK
2019-06-17 12:51:02,992 INFO  - 'Akufen' - 'My Way' - 'Skidoos' - OK
2019-06-17 12:51:02,992 INFO  - 'Trentemøller' - 'The Trentemøller Chronicles' - 'Les Dijinns (Trentemøller Remix)' - OK
2019-06-17 12:51:02,992 INFO  - 'Jem Haynes & SOAME' - 'Streets EP' - 'Mountain Road' - OK
2019-06-17 12:51:02,992 INFO  - 'Plain Pitts' - 'Requinto' - 'Requinto - Jay Shepheard Remix' - MISSING, searching for file 'Requinto - Jay Shepheard Remix.mp3'...
2019-06-17 12:51:03,062 INFO  - 'Plain Pitts' - 'Requinto' - 'Requinto - Jay Shepheard Remix' - Found one matching filename, relocating in rekordbox to '/Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Plain Pits/Requinto/Requinto - Jay Shepheard Remix.mp3'
2019-06-17 12:51:03,062 INFO  - 'Konstantin Sibold' - 'Secret Weapons EP Part 5' - 'Madeleine (Original Mix)' - OK
2019-06-17 12:51:03,062 INFO  - 'Someone Else' - 'Pillowface' - 'Pillowface (Original Mix)' - OK
2019-06-17 12:51:03,062 INFO  - 'Pachanga Boys' - 'Girlcatcher' - 'Time (Original Mix)' - OK
2019-06-17 12:51:03,062 INFO  - 'Capricorn' - 'In Order to Dance' - '20 Hz' - OK
2019-06-17 12:51:03,063 INFO  - 'Nathan Fake' - 'Outhouse Remixes Part 2' - 'Outhouse - Valentino Kanzyani Remix' - OK
2019-06-17 12:51:03,063 INFO  - 'Orbital' - 'Orbital II' - 'Halcyon + On + On' - OK
2019-06-17 12:51:03,063 INFO  - 'Tsunami One and BT' - 'Singles' - 'Hip Hop Phenomenon (Bassbin Twins Edit)' - MISSING, searching for file '01 Hip Hop Phenomenon (Bassbin Twins Edit).mp3'...
2019-06-17 12:51:03,070 WARN  - 'Tsunami One and BT' - 'Singles' - 'Hip Hop Phenomenon (Bassbin Twins Edit)' - Couldn't find '01 Hip Hop Phenomenon (Bassbin Twins Edit).mp3', track will remain in rekordbox as a missing file
2019-06-17 12:51:03,071 INFO  - 'Nightmares On Wax' - 'Mind Elevation' - 'Date With Destiny' - OK
2019-06-17 12:51:03,071 INFO  - 'Minnie Riperton' - 'Defected In The House - Louis Vega' - 'Inside My Love' - MISSING, searching for file '3-09 Inside My Love.m4a'...
2019-06-17 12:51:03,078 INFO  - 'Minnie Riperton' - 'Defected In The House - Louis Vega' - 'Inside My Love' - Found one matching filename, relocating in rekordbox to '/Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Compilations/Defected In The House - Louie Vega/3-09 Inside My Love.m4a'
2019-06-17 12:51:03,078 INFO  - 'Stanton Warriors' - 'Stanton Remixed' - 'Who Are The Warriors - Basskleph Remix' - MISSING, searching for file '01 Who Are The Warriors - Basskleph Remix.mp3'...
2019-06-17 12:51:03,088 WARN  - 'Stanton Warriors' - 'Stanton Remixed' - 'Who Are The Warriors - Basskleph Remix' - Found more than one matching filename so it's not safe to automatically relocate. Matches were:
/Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Stanton Warriors/Stantons Remixed/01 Who Are The Warriors - Basskleph Remix.mp3
/Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Stanton Warriors/Stantons Remixed Duplicate/01 Who Are The Warriors - Basskleph Remix.mp3
2019-06-17 12:51:03,096 INFO  - Checking for files in the search directory which don't exist in rekordbox...
2019-06-17 12:51:03,117 INFO  - 2 files in the search directory don't exist in rekordbox:
/Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Anti Serum/Singles/01 Bang Tha Drums.mp3
/Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Blunted Dummies With A Longer Name Than Necessary/Wild Pitch - The Story - Someone Went Nuts Naming The Album To Try And Pack As Much Info Into It/01 Blunted Dummies - House For All (DJ Pierre Wild PiTcH Mix).mp3
2019-06-17 12:51:03,118 INFO  - Checking for files with a path too long for rekordbox...
2019-06-17 12:51:03,127 INFO  - 1 file paths are too long for rekordbox (255 character limit):
/Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Blunted Dummies With A Longer Name Than Necessary/Wild Pitch - The Story - Someone Went Nuts Naming The Album To Try And Pack As Much Info Into It/01 Blunted Dummies - House For All (DJ Pierre Wild PiTcH Mix).mp3
2019-06-17 12:51:03,128 INFO  - Finished analysing rekordbox collection
2019-06-17 12:51:03,132 INFO  - 
************* Results Summary *************
Total tracks in collection: 17
Tracks OK: 13
Tracks repaired: 2
Tracks with multiple matches: 1
Tracks with missing files: 1
Tracks with path too long: 1
Tracks on disk but not in rekordbox: 2
*******************************************
2019-06-17 12:51:03,133 INFO  - Generating repaired rekordbox collection...
2019-06-17 12:51:03,146 INFO  - Transforming XML...
2019-06-17 12:51:03,233 INFO  - Saving new XML to /Users/Ed/Documents/rekordbox/fixed-library.xml...
2019-06-17 12:51:03,255 INFO  - Finished generating repaired rekordbox collection
2019-06-17 12:51:03,268 INFO  - For a detailed report, see here: /Users/Ed/Documents/rekordbox/fixed-library.report.txt
2019-06-17 12:51:03,268 INFO  - Completed successfully
```

#### rekordbox Repair's detailed report (saved as "fixed-library.report.txt")
```text
rekordbox Repair Report


Summary

Total tracks in collection: 17
Tracks OK: 13
Tracks repaired: 2
Tracks with multiple matches: 1
Tracks with missing files: 1
Tracks with path too long: 1
Tracks on disk but not in rekordbox: 2


Repaired tracks

The original files for the following tracks were not found in their expected locations, but were found elsewhere and repaired versions have been written to the output XML file:

'Minnie Riperton' - 'Defected In The House - Louis Vega' - 'Inside My Love'
Old: /Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Compilations/Defected In The House - Louis Vega/3-09 Inside My Love.m4a
New: /Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Compilations/Defected In The House - Louie Vega/3-09 Inside My Love.m4a

'Plain Pitts' - 'Requinto' - 'Requinto - Jay Shepheard Remix'
Old: /Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Plain Pitts/Requinto/Requinto - Jay Shepheard Remix.mp3
New: /Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Plain Pits/Requinto/Requinto - Jay Shepheard Remix.mp3


Tracks with multiple matches

The original files for the following tracks were not found in their expected locations, but multiple files with the same name were found elsewhere:

'Stanton Warriors' - 'Stanton Remixed' - 'Who Are The Warriors - Basskleph Remix'
Old:
/Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Stanton Warriors/Stanton Remixed/01 Who Are The Warriors - Basskleph Remix.mp3

New (Potentials):
/Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Stanton Warriors/Stantons Remixed/01 Who Are The Warriors - Basskleph Remix.mp3
/Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Stanton Warriors/Stantons Remixed Duplicate/01 Who Are The Warriors - Basskleph Remix.mp3


Tracks with missing files

The files for the following tracks couldn't be found anywhere in the specified search directory:

'Tsunami One and BT' - 'Singles' - 'Hip Hop Phenomenon (Bassbin Twins Edit)'
Missing: /Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Tsunami One and BT/Singles/01 Hip Hop Phenomenon (Bassbin Twins Edit).mp3


Tracks with path too long

rekordbox has a limit of 255 characters for the full path to a track's file, e.g. "/Users/User/Music/Library/Artist/Album/Music File.mp3" is 53 characters long.  The following files exceed that limit and therefore can't be imported to rekordbox:

/Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Blunted Dummies With A Longer Name Than Necessary/Wild Pitch - The Story - Someone Went Nuts Naming The Album To Try And Pack As Much Info Into It/01 Blunted Dummies - House For All (DJ Pierre Wild PiTcH Mix).mp3


Tracks on disk but not in rekordbox

The following files exist in the specified search directory but haven't yet been imported into rekordbox:

/Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Anti Serum/Singles/01 Bang Tha Drums.mp3
/Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Blunted Dummies With A Longer Name Than Necessary/Wild Pitch - The Story - Someone Went Nuts Naming The Album To Try And Pack As Much Info Into It/01 Blunted Dummies - House For All (DJ Pierre Wild PiTcH Mix).mp3
```

#### Step 3: Used report to fix issues that needed manual intervention
- Per report's "Tracks with multiple matches" section, using Finder deleted duplicate Stanton Warriors track "01 Who Are The Warriors - Basskleph Remix.mp3"
- Per report's "Tracks with path too long" section, using iTunes edited the Blunted Dummies track's artist name from "Blunted Dummies With A Longer Name Than Necessary" to "Blunted Dummies", and updated album name from "Wild Pitch - The Story - Someone Went Nuts Naming The Album To Try And Pack As Much Info Into It" to "Wild Pitch - The Story".  Now with a shorter path, I was able to import this track into rekordbox successfully.
- Per report's "Tracks on disk but not in rekordbox" section, imported Anti-Serum's "Bang Tha Drums" track into rekordbox
- After fixing those issues, repeated steps 1 and 2 above. The refreshed report was already looking better:
```text
rekordbox Repair Report


Summary

Total tracks in collection: 19
Tracks OK: 15
Tracks repaired: 3
Tracks with multiple matches: 0
Tracks with missing files: 1
Tracks with path too long: 0
Tracks on disk but not in rekordbox: 0


Repaired tracks

The original files for the following tracks were not found in their expected locations, but were found elsewhere and repaired versions have been written to the output XML file:

'Minnie Riperton' - 'Defected In The House - Louis Vega' - 'Inside My Love'
Old: /Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Compilations/Defected In The House - Louis Vega/3-09 Inside My Love.m4a
New: /Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Compilations/Defected In The House - Louie Vega/3-09 Inside My Love.m4a

'Plain Pitts' - 'Requinto' - 'Requinto - Jay Shepheard Remix'
Old: /Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Plain Pitts/Requinto/Requinto - Jay Shepheard Remix.mp3
New: /Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Plain Pits/Requinto/Requinto - Jay Shepheard Remix.mp3

'Stanton Warriors' - 'Stanton Remixed' - 'Who Are The Warriors - Basskleph Remix'
Old: /Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Stanton Warriors/Stanton Remixed/01 Who Are The Warriors - Basskleph Remix.mp3
New: /Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Stanton Warriors/Stantons Remixed/01 Who Are The Warriors - Basskleph Remix.mp3


Tracks with missing files

The files for the following tracks couldn't be found anywhere in the specified search directory:

'Tsunami One and BT' - 'Singles' - 'Hip Hop Phenomenon (Bassbin Twins Edit)'
Missing: /Users/Ed/Music/rekordbox iTunes/iTunes Media/Music/Tsunami One and BT/Singles/01 Hip Hop Phenomenon (Bassbin Twins Edit).mp3
```

#### Step 4: Imported repaired tracks
- In rekordbox, File menu... Display All Missing Files window, deleted all of them since for 3 tracks we have repaired versions ready to re-import, and for 1 track the file is genuinely gone from the iTunes music folder.  **It's important to delete broken tracks from the collection before importing the repaired versions**.
- In rekordbox Preferences window... View... Layout... section, enabled "rekordbox xml"
- In rekordbox Preferences window... Advanced... rekordbox xml section, pointed the "Imported Library" to the "fixed-library.xml" file generated by rekordbox Repair:

![rekordbox xml preferences](/images/04_rekordbox_xml_prefs.png?raw=true "rekordbox xml Preferences")

- Opened the "rekordbox xml" area in the main window to see the 3 repaired tracks listed, with cue points intact, and also the two playlists these tracks belonged to:  

![rekordbox xml tracks](/images/05_rekordbox_xml_tracks.png?raw=true "rekordbox xml Tracks")

![rekordbox xml playlist](/images/06_rekordbox_xml_playlist.png?raw=true "rekordbox xml Playlist")

- Drag-dropped all 3 repaired tracks from "rekordbox xml"... "All Tracks" into the main collection
- For each playlist in the "rekordbox xml" area, drag-dropped the repaired tracks back into the original playlists
- Selected all tracks in the main rekordbox collection, right-clicked, then selected "Reload Tags". This ensured that any artist and album names which had been renamed in iTunes were reflected correctly in rekordbox.

#### Repaired collection and playlists

![repaired collection](/images/07_repaired_collection.png?raw=true "Repaired collection")

![repaired playlist](/images/08_repaired_playlist.png?raw=true "Repaired playlist")

#### Final rekordbox Repair report
After fixing everything, repeated steps 1 and 2 above. At that point rekordbox Repair reported no issues:
```text
************* Results Summary *************
Total tracks in collection: 18
Tracks OK: 18
Tracks repaired: 0
Tracks with multiple matches: 0
Tracks with missing files: 0
Tracks with path too long: 0
Tracks on disk but not in rekordbox: 0
*******************************************
``` 


### How to install in MacOS
- Download the latest release from https://github.com/edkennard/rekordbox-repair/releases.
- Double-click the downloaded file to extract the tool into a folder "rekordbox-repair-0.1"
- Move the extracted "rekordbox-repair-0.1" folder wherever you wish on your disk. The recommended location is "/Users/You/Applications/rekordbox-repair-0.1"
- Check your installation is working by opening a Terminal window via Applications/Utilities/Terminal.app then enter two commands per below:
```bash
DJLaptop:~ You$ cd "/Users/You/Applications/rekordbox-repair-0.1"

DJLaptop:rekordbox-repair-0.1$ bin/rekordbox-repair --help
```
- The tool should output its help output per the "Help" section below.

### How to install in Windows
- Download the latest release from https://github.com/edkennard/rekordbox-repair/releases.
- Double-click the downloaded file then follow the steps in the setup wizard.
- Check your installation is working by opening a Command Prompt window via Start Menu... Windows System... Command Prompt then enter this command:
```bash
C:\Users\You> rekordbox-repair --help
```
- The tool should output its help output per the "Help" section below.

### Help
Calling the tool with the "--help" option will give you the following info:
```bash
Usage: rekordbox-repair [options]
  
  -i, --input-xml-file <file>
                           Input rekordbox XML file to analyse, e.g. '-i /Users/You/Documents/rekordbox/library.xml'
  -o, --output-xml-file <file>
                           Output rekordbox XML file to write repaired version to, e.g. '-o /Users/You/Documents/rekordbox/library-fixed.xml'
  -s, --search-directory <file>
                           Directory to search for missing files to relocate, e.g. '-s /Users/You/Music/iTunes'
  -r, --output-repaired-tracks-only <boolean>
                           Only write repaired tracks to the output rekordbox XML file, rather than the entire library. By default set to true, set to false if you want to write everything in order to rebuild your entire library
  --help                   Prints this usage text
```

### How to use in MacOS
- Export your rekordbox collection to an XML file.  This can be done via rekordbox's File menu... "Export Collection in xml format" tool.  A recommended location to save this is "/Users/You/Documents/rekordbox/library.xml"
- Open a Terminal window via Applications/Utilities/Terminal.app
- Change to the rekordbox Repair folder (example assumes the location is "/Users/You/Applications/rekordbox-repair-0.1":
```bash
DJLaptop:~ You$ cd "/Users/You/Applications/rekordbox-repair-0.1"
```
- Run the tool, adjusting the values according to your own computer's disk locations:
```bash
DJLaptop:rekordbox-repair-0.1 You$ bin/rekordbox-repair -i "/Users/You/Documents/rekordbox/original-library.xml" -o "/Users/You/Documents/rekordbox/fixed-library.xml" -s "/Users/You/Music/iTunes"
```

### How to use in Windows
- Export your rekordbox collection to an XML file.  This can be done via rekordbox's File menu... "Export Collection in xml format" tool.  A recommended location to save this is "C:\Users\You\Documents\rekordbox\library.xml"
- Open a Command Prompt window via Start Menu... Windows System... Command Prompt
- Run the tool, adjusting the values according to your own computer's files and folders:
```bash
C:\Users\You> rekordbox-repair -i "C:\Users\You\Documents\rekordbox\original-library.xml" -o "C:\Users\You\Documents\rekordbox\fixed-library.xml" -s "C:\Users\You\Music\iTunes"
```

### Future plans
- In the interest of expediency I wrote this initial version as a command line tool only, but with more time it would be better to build a full-blown user interface for it and package as a MacOS app which can be installed into the main Applications area, and a Windows app which can be installed into the Start Menu etc.
- Add support for specifying multiple search directories rather than only one. For some people their collections exist across multiple disks.
- Implement a more intelligent search for missing files where the file may have changed name but it can still be matched based on other criteria, e.g. file size, metadata, the names of its parent folders, the Levenshtein distance between the original filename and the new filename, etc.
- Is there anything you think should be added, changed or fixed? Please submit your thoughts to the [Issues](https://github.com/edkennard/rekordbox-repair/issues) area.
