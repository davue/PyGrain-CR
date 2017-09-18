# PyGrain-CR
The Color Recognition we used for our project in the HackZurich 2017.
Go here to see it being used in the [Frontend](https://github.com/nycooookie/hackathon_webinterface).

##Usage
If supplied with the following arguments:

`java -jar grain.jar <imagepath> <slicingsize>`

It will process the image and print it's results in json format to stdout. The json contains slicing information and for each slice the coordinates and the recognized category.

Without arguments, it will go into debug mode and you can write your arguments to stdin. Every three arguments are parsed like this:

`<imagepath> <slicingsize> <category>`

The output is an image containing only the slices which where categorized as the given category. All other slices are colored pink for easier distinction.