package com.Ravi.Mid_Marks;

public class Student {
	private int sNo;
	private String rollNo;
	private String name;
	private int q1a;
	private int q1b;
	private int q2a;
	private int q2b;
	private int q3;
	private int assignment;
	private int quiz;
	private int totalMid1;

	public Student(int sNo, String rollNo, String name, int q1a, int q1b, int q2a, int q2b, int q3, int assignment,
			int quiz, int totalMid1) {
		this.sNo = sNo;
		this.rollNo = rollNo;
		this.name = name;
		this.q1a = q1a;
		this.q1b = q1b;
		this.q2a = q2a;
		this.q2b = q2b;
		this.q3 = q3;
		this.assignment = assignment;
		this.quiz = quiz;
		this.totalMid1 = totalMid1;
	}

	public int getsNo() {
		return sNo;
	}

	public String getRollNo() {
		return rollNo;
	}

	public String getName() {
		return name;
	}

	public int getQ1a() {
		return q1a;
	}

	public int getQ1b() {
		return q1b;
	}

	public int getQ2a() {
		return q2a;
	}

	public int getQ2b() {
		return q2b;
	}

	public int getQ3() {
		return q3;
	}

	public int getAssignment() {
		return assignment;
	}

	public int getQuiz() {
		return quiz;
	}

	public int getTotalMid1() {
		return totalMid1;
	}

	public void setsNo(int sNo) {
		this.sNo = sNo;
	}

	public void setRollNo(String rollNo) {
		this.rollNo = rollNo;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setQ1a(int q1a) {
		this.q1a = q1a;
	}

	public void setQ1b(int q1b) {
		this.q1b = q1b;
	}

	public void setQ2a(int q2a) {
		this.q2a = q2a;
	}

	public void setQ2b(int q2b) {
		this.q2b = q2b;
	}

	public void setQ3(int q3) {
		this.q3 = q3;
	}

	public void setAssignment(int assignment) {
		this.assignment = assignment;
	}

	public void setQuiz(int quiz) {
		this.quiz = quiz;
	}

	public void setTotalMid1(int totalMid1) {
		this.totalMid1 = totalMid1;
	}
}
