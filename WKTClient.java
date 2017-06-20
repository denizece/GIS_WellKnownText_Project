/******************************************************************************
  *  Author:       Deniz Ece Aktan
  *  Compilation:  javac WKTClient.javac WKTReader.javac WKTWriter.java
  *  Execution:    java WKTClient
  * 
  *  Dependencies: com.sinergise.geometry.*, WKTReader, WKTWriter
  * 
  *  This is an example client for WKTReader and WKTWriter Classes
  *
  ******************************************************************************/

import java.io.IOException;
import java.text.ParseException;

import com.sinergise.geometry.Geometry;

public class WKTClient {

	public static void main(String[] args) {
		WKTReader wktreader = new WKTReader();
		WKTWriter wktwriter = new WKTWriter();
		Geometry g;
		try {
			g = wktreader.read("LINESTRING (30 10, 10 30, 40 40)");
			System.out.println(wktwriter.write(g));
			g = wktreader.read("GEOMETRYCOLLECTION (POINT (4 6),"
					+ " LINESTRING (4 6, 7 10))");
			System.out.println(wktwriter.write(g));
			g = wktreader.read("LINESTRING EMPTY");
			System.out.println(wktwriter.write(g));
			g = wktreader.read("MULTILINESTRING ("
					+ "(30 10, 10 30, 40 40, 30 10), "
					+ "(130 110, 110 130,"
					+ " 140 140, 130 110),"
					+ " (230 210, 210 230,"
					+ " 240 240, 230 210))");
			System.out.println(wktwriter.write(g));
			g = wktreader.read("POLYGON ((330 310, 310 330,"
					+ " 340 340, 330 310))");
			System.out.println(wktwriter.write(g));
			g = wktreader.read("POLYGON "
					+ "((330 310, 310 330,"
					+ " 340 340, 330 310),"
					+ "(30 10, 10 30, 40 40, 30 10),"
					+ " (130 110, 110 130,"
					+ " 140 140, 130 110), "
					+ "(230 210, 210 230, "
					+ "240 240, 230 210))");
			System.out.println(wktwriter.write(g));
			g = wktreader.read("MULTIPOLYGON"
					+ " (((330 310, 310 330,"
					+ " 340 340, 330 310)),"
					+ " ((330 310, 310 330,"
					+ " 340 340, 330 310),"
					+ "(30 10, 10 30, 40 40, 30 10),"
					+ " (130 110, 110 130,"
					+ " 140 140, 130 110),"
					+ " (230 210, 210 230,"
					+ " 240 240, 230 210)))");
			System.out.println(wktwriter.write(g));
			g = wktreader.read("MULTIPOINT ((4 6), (6 8), (8 10))");
			System.out.println(wktwriter.write(g));

		} catch (ParseException | IOException
				| InstantiationException 
				| IllegalAccessException 
				| ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

}
