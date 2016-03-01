# Imagine Data Conversion & Obfuscation Utility
* Encode Data as Images
* Encode Data into Existing Images with Little to No Visible Difference
..* 25% & 50% Modes (File Data / Image Data Ratio)
* Single/Multi-File Support
* GUI or Command Line
* Tested on Ubuntu 14, planned support for Windows in near future

### Dependencies
* Java 8

### Downloading
Download Imagine.zip file which should be located in the top level of the repo at https://github.com/telgin/Imagine.git . When you unzip the archive, you should get the jar file (Imagine.jar), a log folder, and a config file. For the moment, there's no installation process, but you do need to run the jar from within this folder because it needs to know where the configuration file is. This will probably change to be something better eventually.

### GUI Usage
Since you don't need to install this for the time being, you can just run the gui from the jar like so:

	java -jar Imagine.jar
	
(Windows may allow you to double click the jar)

### CLI Usage (also available through '--help')
```text
<pre>Command Syntax</pre>
<pre>imagine --open -a <algorithm> -i <file> [-o <folder>] [-k [keyfile]]</pre>
<pre>imagine --embed -a <algorithm> -i <file/folder> [-o <folder>] [-k [keyfile]]</pre>
<pre>imagine --extract -a <algorithm> -i <file/folder> [-o <folder>] [-k [keyfile]]</pre>
```
<pre>
--open
    open an archive and selectively extract its contents
--embed
    embed data into a supported format (create archives)
--extract
    extract all data from an archive file or folder of multiple archives
--gui
    open the gui (default) (not all flags supported)
</pre>
<pre>-a <algorithm>
    algorithm preset name
-i <file/folder>
    input file or folder (multiple flags supported)
-I <file>
    a file containing a list of input files (1 per line)
-o <folder>
    output folder
-k [file]
    key file or empty for password (optional)
-p
    prompt for a password
-P <"name=value">
    override algorithm parameter (quotes optional)
-r <file>
    create a report file of which archive each file was added to
</pre>

### Psuedo-Security Disclaimer/Warning:
While the security within this program should be good enough to keep your friends out of your archives, I have not hired security specialists to look at this software and as such I make no claims or promises about how secure it is. This was just a casual fun project for me. Could I break the security? No. Could a team of security researchers break it? Possibly. I don't know how they would, but that doesn't mean anything. **If you want your files to cryptographically secure, encrypt them before adding them to archives with this software!** The primary usefulness of this software should come from the ability to hide files, not to secure them outright.

### Planned Features (in rough order of priority) (also open to suggestions)
* Official Windows support (It's java so it might already work?)
* Bulk extraction from files using GUI (currently only supported in cli)
* Right click menus (e.g. right click file from os, select extract, select create archive)
* Open archive given a url
* Open archive from a screenshot
* Lossy output format support (jpeg?)
* Alternative media output (videos, music?)

### Documentation
Before everyone yells at me because "obfuscation is not a superior paradigm to encryption", don't worry, I agree. BUT... obfuscation can be really cool and there's nothing stopping anyone from using it in conjunction with encryption, so there!

#### Algorithms:
Currently I have three algorithms, with plans to add more. My expectation is that "Image Overlay" will be the most useful to everyone, but I've included the others because why not?

##### Image Overlay
**Overview**
(For clarity, target files are files for which you want to 'embed data into', input files are files you want to embed.)

This algorithm allows you to encode data as an 'overlay' to one or multiple existing image files. At a 25% ratio of file data to image data, it will be just about impossible to tell the difference between the original image and the output image. At least on the one's I've tested, I cannot tell the difference even when zoomed in. At 50%, you may be able to see some differences in some images, but even then maybe not. Regardless, the differences shouldn't really jump out at anyone. They just look like normal compression artifacts and everyone's pretty used to seeing that. (Ex. quality of jpg vs. png)

One thing you may notice is depending on the composition and format of target image files, you may see an overall size increase in the output image. There are two reasons for this. First if your input image was compressed in a lossy format (jpg for instance), the file will need to be converted to a lossless format (png) before data can be added. The algorithm relies on being able to know the exact color values of every pixel because that is where the file data is stored. As the png is saved, it will be recompressed but this time in a lossless way so it may still be larger than the original. A good example of this would be images captured from a high megapixel cellphone. Such images may be very large (~5000x~3000) but if they're in jpg format as they usually are, much of the original data may have been thrown away. So when you convert such an image from jpg to png, it may increase from ~5-7MB to ~40MB just from that. A second reason for a size increase might be because the resulting image has more entropy than the original image making it harder for the png format to losslessly compress. The main reason for this is that the overlay is much harder to see if the changes to pixel colors have a random distribution. However, even though there is a size increase of the original image, the % ratios are based on the size of the output image, meaning that an image which is output with a size of 40MB will have space for either 10MB (25%) or 20MB (50%) of file data.

##### Image
**Overview**
The image algorithm is much like the image overlay algorithm running at a 100% ratio. In other words, each pixel is 100% file data and as such no target images are required. I'd think the main use case for this would be if you weren't concerned with hiding the fact that an image contained some kind of data and just wanted the maximum number of file bytes per image. This might come in handy if you were looking to store files on an image hosting website. (which does not compress the images you upload) As with all other formats so far, the input data is added to the archive in a random pattern, you will see random colors for each pixel. Potentially the randomness could hide the fact that a non random sequence of bytes can be derived from it? Probably depends on who you show it to.

##### Text
**Overview**
This is so far the only non-image output format. Right now it is questionably useful, but it was helpful for me when testing the core protocol. I left it in, in case someone has a use case for it or just wants to test things themselves. Specifically, this outputs a number of text files in base64 or hex. You can adjust the size of the text files with a parameter of the algorithm. The files are padded, so they should all be exactly the same size.

