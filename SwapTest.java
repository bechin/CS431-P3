import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Collections;

public class SwapTest{

	private List<Job> jobs = new ArrayList<>();
	private final int MEMORY_SIZE = 100;
	private Segment start = new Segment(0, 0, MEMORY_SIZE, null);
	private Segment lastFit = start;

	public static void main(String[] args){
		new SwapTest();
	}

	public SwapTest(){
		String command = "command";
		Scanner kb = new Scanner(System.in);
		while(!command.equals("exit")){
			System.out.print("> ");
			command = kb.nextLine();
			String[] commandArgs = command.split(" ");
			switch(commandArgs[0]){
				case"add":
					String[] jobArgs = commandArgs[1].split(",");
					Job j = new Job(Integer.parseInt(jobArgs[0]),
									Integer.parseInt(jobArgs[1]));
					if(!jobs.contains(j)){
						jobs.add(j);
					}
					else{
						System.out.println("pid already exists.");
					}
					break;
				case"jobs":
					printJobs();
					break;
				case"list":
					list();
					break;
				case"ff":
					if(!firstFit(Integer.parseInt(commandArgs[1])))
						System.out.println("Allocation failed.");
					break;
				case"nf":
					if(!nextFit(Integer.parseInt(commandArgs[1])))
						System.out.println("Allocation failed.");
					break;
				case"bf":
					if(!bestFit(Integer.parseInt(commandArgs[1])))
						System.out.println("Allocation failed.");
					break;
				case"wf":
					if(!worstFit(Integer.parseInt(commandArgs[1])))
						System.out.println("Allocation failed.");
					break;
				case"de":
					if(!deallocate(Integer.parseInt(commandArgs[1])))
						System.out.println("Job not allocated.");
					break;
				case"exit":
					System.out.println("Goodbye!");
					break;
				default:
					System.out.println("Invalid Command!");
			}
		}
	}

	private void printJobs(){
		for(Job j : jobs){
			System.out.print(j);
		}
		System.out.println();
	}

	private void list(){
		Segment trav = start;
		while(trav != null){
			System.out.print(trav);
			if(trav.getNext() != null)
				System.out.print(" ");
			trav = trav.getNext();
		}
		System.out.println();
	}

	private Job getJob(int pid){
		for(Job j : jobs){
			if(j.getPid() == pid)
				return j;
		}
		return null;
	}

	private boolean alreadyAllocated(Job j){
		return j != null? j.allocated : false;
	}

	private boolean firstFit(int pid){
		if(alreadyAllocated(getJob(pid)))
			return false;
		Job j = getJob(pid);
		Segment trav = start;
		while(trav != null){
			if(trav.getPid() == 0 && j.getSize() <= trav.getLength()){
				trav.setPid(pid);
				if(j.getSize() < trav.getLength()){
					int remainingHoleStart = trav.getStart() + j.getSize();
					int remainingHoleSize = trav.getLength() - j.getSize();
					Segment remainingHole = new Segment(0, remainingHoleStart,
													    remainingHoleSize, trav.getNext());
					trav.setLength(j.getSize());
					trav.setNext(remainingHole);
				}
				j.allocated = true;
				return true;
			}
			trav = trav.getNext();
		}
		return false;
	}

	private boolean nextFit(int pid){
		if(alreadyAllocated(getJob(pid)))
			return false;
		Job j = getJob(pid);
		Segment trav = lastFit;
		while(trav != null){
			if(trav.getPid() == 0 && j.getSize() <= trav.getLength()){
				trav.setPid(pid);
				if(j.getSize() < trav.getLength()){
					int remainingHoleStart = trav.getStart() + j.getSize();
					int remainingHoleSize = trav.getLength() - j.getSize();
					Segment remainingHole = new Segment(0, remainingHoleStart,
													    remainingHoleSize, trav.getNext());
					trav.setLength(j.getSize());
					trav.setNext(remainingHole);
				}
				j.allocated = true;
				lastFit = trav.getNext();
				if(lastFit == null)
					lastFit = start;
				return true;
			}
			trav = trav.getNext();
		}
		return false;
	}

	private boolean bestFit(int pid){
		if(alreadyAllocated(getJob(pid)))
			return false;
		Job j = getJob(pid);
		Segment trav = start;
		Queue<Segment> holes = new PriorityQueue<>();
		while(trav != null){
			if(trav.getPid() == 0)
				holes.add(trav);
			trav = trav.getNext();
		}
		Segment bestFit = holes.poll();
		while(j.getSize() > bestFit.getLength())
			bestFit = holes.poll();
		if(bestFit == null)
			return false;
		bestFit.setPid(pid);
		if(j.getSize() < bestFit.getLength()){
			int remainingHoleStart = bestFit.getStart() + j.getSize();
			int remainingHoleSize = bestFit.getLength() - j.getSize();
			Segment remainingHole = new Segment(0, remainingHoleStart,
												remainingHoleSize, bestFit.getNext());
			bestFit.setLength(j.getSize());
			bestFit.setNext(remainingHole);
		}
		j.allocated = true;
		return true;
	}

	private boolean worstFit(int pid){
		if(alreadyAllocated(getJob(pid)))
			return false;
		Job j = getJob(pid);
		Segment trav = start;
		Queue<Segment> holes = new PriorityQueue<>(11, Collections.reverseOrder());
		while(trav != null){
			if(trav.getPid() == 0)
				holes.add(trav);
			trav = trav.getNext();
		}
		Segment bestFit = holes.poll();
		while(j.getSize() > bestFit.getLength())
			bestFit = holes.poll();
		if(bestFit == null)
			return false;
		bestFit.setPid(pid);
		if(j.getSize() < bestFit.getLength()){
			int remainingHoleStart = bestFit.getStart() + j.getSize();
			int remainingHoleSize = bestFit.getLength() - j.getSize();
			Segment remainingHole = new Segment(0, remainingHoleStart,
												remainingHoleSize, bestFit.getNext());
			bestFit.setLength(j.getSize());
			bestFit.setNext(remainingHole);
		}
		j.allocated = true;
		return true;
	}

	private boolean deallocate(int pid){
		if(!alreadyAllocated(getJob(pid)))
			return false;
		Segment trav = start;
		while(trav != null){
			if(trav.getPid() == pid){
				trav.setPid(0);
				getJob(pid).allocated = false;
				break;
			}
			trav = trav.getNext();
		}
		trav = start;
		while(trav != null){
			if(trav.getNext() == null)
				break;
			Segment next = trav.getNext();
			if(trav.getPid() == 0 && next.getPid() == 0){
				trav.setLength(trav.getLength() + next.getLength());
				trav.setNext(next.getNext());
				next = null;
				continue;
			}
			trav = trav.getNext();
		}
		return true;
	}

	final class Segment implements Comparable<Segment>{

		private int pid;
		private int start;
		private int length;
		private Segment next;

		public Segment(int pid, int start, int length, Segment next) {
			this.pid = pid;
			this.start = start;
			this.length = length;
			this.next = next;
		}
		public int getPid() {
			return pid;
		}

		public int getStart() {
			return start;
		}

		public int getLength() {
			return length;
		}

		public Segment getNext() {
			return next;
		}

		public void setPid(int pid) {
			this.pid = pid;
		}

		public void setStart(int start) {
			this.start = start;
		}

		public void setLength(int length) {
			this.length = length;
		}

		public void setNext(Segment next) {
			this.next = next;
		}

		public int compareTo(Segment that){
			if(this.length < that.getLength())
				return -1;
			else if(this.length == that.getLength())
				return 0;
			else
				return 1;
		}

		@Override
		public String toString() {
			return String.format("(%d %d %d)", pid, start, length);
		}

	}

	final class Job{

		private final int pid;
		private final int size;
		protected boolean allocated;

		public Job(int pid, int size) {
			this.pid = pid;
			this.size = size;
			allocated = false;
		}

		public int getPid() {
			return pid;
		}

		public int getSize() {
			return size;
		}

		public boolean equals(Job that){
			return this.pid == that.getPid();
		}

		@Override
		public String toString() {
			return String.format("[%d %d]", pid, size);
		}

	}

}
