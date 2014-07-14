package com.jenjinstudios.demo.server;

import java.util.Scanner;

/**
 * @author Caleb Brinkman
 */
public class Main
{
	public static void main(String[] args) throws InterruptedException {
		Scanner input = new Scanner(System.in);

		String readLine = input.nextLine();
		while (readLine != null && !"quit".equals(readLine))
		{
			Thread.sleep(100);
		}
	}
}
