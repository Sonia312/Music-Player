package project;

import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
import java.util.HashSet;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * 
 * Class for storing the details of the song
 *
 */

class SongDetails{
	
	String filePath,songName;
	ArrayList<String> singers;
	
	SongDetails(String filePath,String songName,ArrayList<String> singers){
		this.filePath=filePath;
		this.songName=songName;
		this.singers=singers;
	}
	
}

/**
 * 
 * Class for importing the data from CSV
 *
 */

class ImportFromCSV{
	
	String file,line;
	BufferedReader reader;
	ArrayList<SongDetails> list; // This list contains details of all songs
	
	ImportFromCSV(){
		list=new ArrayList<>();
		file="C:\\Users\\niade\\eclipse-workspacecollege\\DSLProject\\src\\project\\DSLProject\\data.csv";
		line="";
		try {
			reader=new BufferedReader(new FileReader(file));
			while((line=reader.readLine())!=null) {
				ArrayList<String> singers=new ArrayList<>();
				String[] row=line.split(",");
				String[] singerList=row[2].split("-");
				for(int i=0;i<singerList.length;i++) {
					singers.add(singerList[i]);
				}
				SongDetails s=new SongDetails(row[0],row[1],singers);
				list.add(s);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
}

/**
 * 
 * All the functions that can be performed in the music player are a part of this class
 *
 */

class MusicAppFunctions{
	
	Scanner sc;
	ImportFromCSV csvData;
	HashSet<String> singerNames;
	boolean status;
	
	MusicAppFunctions(){
		sc=new Scanner(System.in);
		csvData=new ImportFromCSV();
		singerNames=new HashSet<>();
		for(int i=0;i<csvData.list.size();i++) {
			for(int j=0;j<csvData.list.get(i).singers.size();j++) {
				singerNames.add(csvData.list.get(i).singers.get(j));
			}
		}
		status=true;
	}
	
	void selectAnOption() {
		System.out.println("\nEnter\n1-To play your favourite artist playlist\n2-To play all songs\n3-To exit : ");
		int n=sc.nextInt();
		sc.nextLine();
		switch(n) {
			case 1:
				playSinger();
				break;
			case 2:
				playAlbum();
				break;
			case 3:
				System.exit(0);
			default:
				System.out.println("\nWrong option entered");
				break;
		}
	}
	
	
	// Plays all the songs
	void playAlbum() {
		MyDoublyLinkedList<SongDetails> allSongs=new MyDoublyLinkedList<>();
		ArrayList<String> arr=new ArrayList<>();
		for(SongDetails s:csvData.list) {
			allSongs.insertAtEnd(s);
			arr.add(s.songName);
		}
		Object[] songs=arr.toArray();
		playMusic(allSongs,allSongs.head, songs);
	}
	
	
	// Plays all the songs of a specific singer
	void playSinger() {
		ArrayList<String> songs=new ArrayList<>();
		String[] arr=new String[singerNames.size()];
		int i=0;
		for(String s:singerNames)arr[i++]=s;
		System.out.println("Enter the index number to access the singer : ");
		for(i=0;i<arr.length;i++)System.out.println(i+1+" - "+arr[i]);
		int opt=sc.nextInt();
		sc.nextLine();
		if(opt>arr.length) {
			System.out.println("No such options available\n");
			playSinger();
		}
		MyDoublyLinkedList<SongDetails> dll=new MyDoublyLinkedList<>();
		for(SongDetails s:csvData.list) {
			if(s.singers.contains(arr[opt-1])) {
				dll.insertAtEnd(s);
				songs.add(s.songName);
			}
		}
		Object[] songList=songs.toArray();
		playMusic(dll,dll.head, songList);
	}
	
	DoublyNode<SongDetails> searchSong(MyDoublyLinkedList<SongDetails> dll,String name){
		
		DoublyNode<SongDetails> ptr=dll.head.next;
		
		if(dll.head.val.songName.compareTo(name)==0) {
			return dll.head;
		}
		
		while(ptr.val.songName.compareTo(name)!=0)ptr=ptr.next;
		
		return ptr;
		
	}
	
	// Plays only a specific song of your choice
	DoublyNode<SongDetails> playSpecificSong(MyDoublyLinkedList<SongDetails> dll, Object[] songs) {
		System.out.println("Enter the index number to access the singer");
		for(int i=0;i<songs.length;i++)System.out.println(i+1+" - "+songs[i]);
		int opt=sc.nextInt();
		sc.nextLine();
		if(opt>songs.length) {
			System.out.println("No such options available\n");
			playSpecificSong(dll,  songs);
		}
		String s=(String)songs[opt-1];
		DoublyNode<SongDetails> d=searchSong(dll, s);
		return d;
	}
	
	// Implements the loop song function
	DoublyNode<SongDetails> playNextOrSameMusic( DoublyNode<SongDetails> currSong) {
		if(status) {
			return currSong.next;
		}
		else {
			return currSong;
		}
	}
	
	void playMusic(MyDoublyLinkedList<SongDetails> dll, DoublyNode<SongDetails> currSong, Object[] songs) {
		try {
			SimpleAudioPlayer obj=new SimpleAudioPlayer(currSong.val.filePath);
			Long time=obj.clip.getMicrosecondLength();
			Timer timer=new Timer();
			TimerTask task=new TimerTask() {
				public void run() {
					if(obj.clip.getMicrosecondPosition()>=time) {
						obj.clip.stop();
						DoublyNode<SongDetails> node=playNextOrSameMusic(currSong);
						playMusic(dll, node, songs);
					}
				}
			};
			timer.scheduleAtFixedRate(task, 0,10000);
			System.out.println("\nPrevious song : "+currSong.prev.val.songName);
			System.out.println("\nCurrently Playing : "+currSong.val.songName);
			System.out.println("\nNext song : "+currSong.next.val.songName);
			while(true) {
				obj.play();
				while(true) {
					System.out.println("\nEnter a choice\n1-To pause\n2-To resume\n3-To restart\n4-Next Song\n5-Previous song\n6-To play specific music from playlist\n7-To keep the song on repeat\n8-To go to menu : ");
					int n;
					try{
						Scanner sc2=new Scanner(System.in);
						n=sc2.nextInt();
						sc2.nextLine();
						switch (n)
						{
							case 1:
								obj.pause();
								break;
							case 2:
								obj.resumeAudio();
								break;
							case 3:
								obj.restart();
								break;
							case 4:
								status=true;
								obj.stop();
								playMusic(dll,currSong.next, songs);
								break;
							case 5:
								status=true;
								obj.stop();
								playMusic(dll,currSong.prev, songs);
								break;
							case 6:
								DoublyNode<SongDetails> d=playSpecificSong(dll, songs);
								obj.stop();
								playMusic(dll, d, songs);
								break;
							case 7:
								status=false;
								break;
							case 8:
								obj.stop();
								selectAnOption();
								break;
							default:
								System.out.println("Wrong option entered");
						
						
						}
					}catch(Exception e) {}
				}
			}
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

public class musicPlayer {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MusicAppFunctions m=new MusicAppFunctions();
		m.selectAnOption();
	}

}
