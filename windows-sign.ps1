signtool.exe sign /sha1 55DE38A5021D780AAC0A38F3904FBB3998C4D273 /t http://timestamp.digicert.com *.msi
signtool.exe verify /pa *.msi