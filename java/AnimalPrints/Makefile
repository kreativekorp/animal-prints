SRCFILES := $(shell find src -name \*.java)
RESFILES := $(shell find src -type f \! -name .\* \! -name \*.java | sed s/^src\\///)
PACKAGES := $(shell find src -name \*.java | sed -E s/^src\\/\|\\/[^\\/]+$$//g | sort | uniq | tr / .)

all: clean bin doc animalprints.jar animalprints.zip Animal\ Prints.app

eclipseall: eclipseclean animalprints.jar animalprints.zip Animal\ Prints.app

clean:
	rm -rf bin
	rm -rf doc
	rm -rf *.jar
	rm -rf *.zip
	rm -rf *.app
	rm -rf *.dmg

eclipseclean:
	rm -rf *.jar
	rm -rf *.zip
	rm -rf *.app
	rm -rf *.dmg

bin: src
	mkdir -p bin
	javac $(JAVACFLAGS) -classpath dep/nrserial.jar:dep/ual.jar -sourcepath src $(SRCFILES) -d bin
	$(foreach res,$(RESFILES),cp src/$(res) bin/$(res);)

doc: src
	mkdir -p doc
	javadoc $(JAVADOCFLAGS) -classpath dep/nrserial.jar:dep/ual.jar -sourcepath src $(PACKAGES) -d doc

animalprints.jar: bin
	export COPYFILE_DISABLE=true
	find bin -name .DS_Store -delete
	jar cmf dep/MANIFEST.MF animalprints.jar -C bin com/kreative/acpattern -C bin com/google/zxing
	chmod +x animalprints.jar

animalprints.zip: dep animalprints.jar
	rm -rf Animal\ Prints
	mkdir -p Animal\ Prints
	cp -f dep/AnimalPrints.exe Animal\ Prints
	cp -f dep/*.jar Animal\ Prints
	cp -f animalprints.jar Animal\ Prints
	zip -r animalprints.zip Animal\ Prints
	rm -rf Animal\ Prints

Animal\ Prints.app: dep animalprints.jar
	mkdir -p Animal\ Prints.app/Contents/MacOS
	mkdir -p Animal\ Prints.app/Contents/Resources/Java
	cp -f dep/PkgInfo Animal\ Prints.app/Contents
	cp -f dep/Info.plist Animal\ Prints.app/Contents
	cp -f dep/universalJavaApplicationStub Animal\ Prints.app/Contents/MacOS/Animal\ Prints
	cp -f dep/leaf-print.icns Animal\ Prints.app/Contents/Resources
	cp -f dep/*.jar Animal\ Prints.app/Contents/Resources/Java
	cp -f animalprints.jar Animal\ Prints.app/Contents/Resources/Java

animalprints.dmg: Animal\ Prints.app
	rm -rf dmgtmp
	mkdir -p dmgtmp
	cp -R Animal\ Prints.app dmgtmp
	hdiutil create -fs HFS+ -volname "Animal Prints" -srcfolder dmgtmp animalprints.dmg
	rm -rf dmgtmp

install: animalprints.jar
	sudo mkdir -p /usr/local/lib/
	sudo cp -f dep/ual.jar /usr/local/lib/ual.jar
	sudo cp -f dep/nrserial.jar /usr/local/lib/nrserial.jar
	sudo cp -f animalprints.jar /usr/local/lib/animalprints.jar
	sudo chmod -f 644 /usr/local/lib/animalprints.jar
	sudo mkdir -p /usr/local/bin/
	sudo cp -f dep/animalprints /usr/local/bin/animalprints
	sudo cp -f dep/acconvert /usr/local/bin/acconvert
	sudo cp -f dep/acview /usr/local/bin/acview
	sudo chmod -f 755 /usr/local/bin/animalprints
	sudo chmod -f 755 /usr/local/bin/acconvert
	sudo chmod -f 755 /usr/local/bin/acview
	sudo mkdir -p /usr/share/icons/hicolor/48x48/apps/
	sudo mkdir -p /usr/share/icons/hicolor/128x128/apps/
	sudo mkdir -p /usr/share/icons/hicolor/512x512/apps/
	sudo cp -f dep/leaf-print-48.png /usr/share/icons/hicolor/48x48/apps/animalprints.png
	sudo cp -f dep/leaf-print-128.png /usr/share/icons/hicolor/128x128/apps/animalprints.png
	sudo cp -f dep/leaf-print-512.png /usr/share/icons/hicolor/512x512/apps/animalprints.png
	sudo chmod -f 644 /usr/share/icons/hicolor/48x48/apps/animalprints.png
	sudo chmod -f 644 /usr/share/icons/hicolor/128x128/apps/animalprints.png
	sudo chmod -f 644 /usr/share/icons/hicolor/512x512/apps/animalprints.png
	sudo mkdir -p /usr/share/applications/
	sudo cp -f dep/animalprints.desktop /usr/share/applications/animalprints.desktop
	sudo chmod -f 644 /usr/share/applications/animalprints.desktop

.PHONY: all eclipseall clean eclipseclean install
