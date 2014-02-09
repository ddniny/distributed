Configfilename = /afs/andrew.cmu.edu/usr5/zhuolinl/18842/Lab0.yaml
Name = alice

all:	
	javac -cp .:lib/snakeyaml-1.13.jar message/*.java record/*.java thread/*.java \
	util/*.java

run:	all
	java -cp .:lib/snakeyaml-1.13.jar message.MessagePasser ${Configfilename} ${Name}	

clean:
	find . -name *.class | xargs rm -rf >> a.out
	find -name *.class | xargs rm -rf >> a.out
	rm a.out
