/******************************************************************************
  *  Author:       Deniz Ece Aktan
  *  Compilation:  javac WKTReader.java
  *  Execution:    java WKTReader
  * 
  *  Dependencies: com.sinergise.geometry 
  * 
  *  This class Tread takes a WKT formatted String,
  *   transformes in into a Geometry object and returns it.
  *
  ******************************************************************************/

import java.io.IOException;
import java.io.StreamTokenizer;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import com.sinergise.geometry.Geometry;
import com.sinergise.geometry.GeometryCollection;
import com.sinergise.geometry.LineString;
import com.sinergise.geometry.MultiLineString;
import com.sinergise.geometry.MultiPoint;
import com.sinergise.geometry.MultiPolygon;
import com.sinergise.geometry.Point;
import com.sinergise.geometry.Polygon;

import util.Coordinate;

public class WKTReader {
	private StreamTokenizer tokenizer;
	private static final String EMPTY = "EMPTY";
	private static final String COMMA = ",";
	private static final String L_P = "(";
	private static final String R_P = ")";
	private HashMap<String, String> geoMap;
	public WKTReader()
	{
		geoMap = new HashMap<String, String>();
		geoMap.put("POINT", "com.sinergise.geometry.Point");
		geoMap.put("LINESTRING", "com.sinergise.geometry.LineString");
		geoMap.put("POLYGON", "com.sinergise.geometry.Poligon");
		geoMap.put("MULTILINESTRING",
				"com.sinergise.geometry.MultiLineString");
		geoMap.put("MULTIPOINT", "com.sinergise.geometry.MultiPoint");
		geoMap.put("MULTIPOLYGON",
				"com.sinergise.geometry.MultiPoligon");
	}
	/**
	 * Transforms the input WKT-formatted String into Geometry object
	 * @throws ParseException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public Geometry read(String wktString) throws ParseException,
	IOException, InstantiationException,
	IllegalAccessException, ClassNotFoundException {
		StringReader reader = new StringReader(wktString);
		tokenizer = new StreamTokenizer(reader);
		tokenizer.resetSyntax();
		tokenizer.wordChars('a', 'z');
		tokenizer.wordChars('A', 'Z');
		tokenizer.wordChars(128 + 32, 255);
		tokenizer.wordChars('0', '9');
		tokenizer.wordChars('-', '-');
		tokenizer.wordChars('+', '+');
		tokenizer.wordChars('.', '.');
		tokenizer.whitespaceChars(0, ' ');
		tokenizer.commentChar('#');
		Geometry geoObj = readFormattedGeoString("");
		return geoObj;	
	}
	private Geometry readFormattedGeoString(String type_inp)
			throws IOException, ParseException,
			InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		String type = "";
		String nextToken;
		Geometry output = null;
		if (!type_inp.isEmpty())
			type = type_inp;
		else {
			type = getNextWord();
			nextToken = getNextEmptyOrOpener();
			if (nextToken.equals(EMPTY)) {
				output = (Geometry) Class.forName(geoMap.get(type)).newInstance();
				return output;
			}
		}
		if (type.equals("POINT")) {
			Coordinate coord = new Coordinate(getNextNumber(),getNextNumber());
			Point point = new Point(coord.getX(),coord.getY());
			getNextCloser();
			output = point;
		}
		else if (type.equalsIgnoreCase("LINESTRING")) {
			Coordinate [] cs = getCoordinates();
			ArrayList<Double> list = new ArrayList<>();
			for (int i = 0; i < cs.length; i++) {
				list.add(cs[i].getX());
				list.add(cs[i].getY());
			}
			double[] arr = new double[list.size()];
			for (int i = 0; i < arr.length; i++) {
				arr[i] = list.get(i);
			}
			output = new LineString(arr);
		}
		else if (type.equalsIgnoreCase("POLYGON")) {
			ArrayList<LineString> holes = new ArrayList<>();
			getNextEmptyOrOpener();
			LineString shell = (LineString) readFormattedGeoString("LINESTRING");
			nextToken = getNextCloserOrComma();
			LineString[] array = null;
			if (!nextToken.equals(R_P))
			{	
				while (nextToken.equals(COMMA)) {
					nextToken = getNextEmptyOrOpener();
					LineString hole = (LineString) readFormattedGeoString("LINESTRING");
					holes.add(hole);
					nextToken = getNextCloserOrComma();
				}
				array = new LineString[holes.size()];
				for (int i = 0; i < holes.size(); i++) {
					array[i] = holes.get(i);
				}
			}
			output = new Polygon(shell, array);
		}
		else if (type.equalsIgnoreCase("MULTIPOINT")) {
			ArrayList<Point> points = new ArrayList<>();
			nextToken = getNextEmptyOrOpener();
			points.add((Point)readFormattedGeoString("POINT"));
			nextToken = getNextCloserOrComma();
			while (nextToken.equals(COMMA)) {
				nextToken = getNextEmptyOrOpener();
				Point p = (Point) readFormattedGeoString("POINT");
				points.add(p);
				nextToken = getNextCloserOrComma();

			}
			Point[] array = new Point[points.size()];
			for (int i = 0; i < points.size(); i++) {
				array[i] = points.get(i);
			}
			output = new MultiPoint(array);
		}
		else if (type.equalsIgnoreCase("MULTILINESTRING")) {
			ArrayList<LineString> lineStrings = new ArrayList<>();
			getNextEmptyOrOpener();
			LineString lineString = (LineString) readFormattedGeoString("LINESTRING");
			lineStrings.add(lineString);
			nextToken = getNextCloserOrComma();
			while (nextToken.equals(COMMA)) {
				nextToken = getNextEmptyOrOpener();
				lineString = (LineString) readFormattedGeoString("LINESTRING");
				lineStrings.add(lineString);
				nextToken = getNextCloserOrComma();
			}
			LineString[] array = new LineString[lineStrings.size()];
			for (int i = 0; i < lineStrings.size(); i++) {
				array[i] = lineStrings.get(i);
			}
			output = new MultiLineString(array);
		}
		else if (type.equalsIgnoreCase("MULTIPOLYGON")) {
			ArrayList<Polygon> polygons = new ArrayList<>();
			getNextEmptyOrOpener();
			Polygon polygon =(Polygon) readFormattedGeoString("POLYGON");
			polygons.add(polygon);
			nextToken = getNextCloserOrComma();
			while (nextToken.equals(COMMA)) {
				nextToken = getNextEmptyOrOpener();              	 
				polygon = (Polygon) readFormattedGeoString("POLYGON");
				polygons.add(polygon);
				nextToken = getNextCloserOrComma();
			}
			Polygon[]array = new Polygon[polygons.size()];
			for (int i = 0; i < polygons.size(); i++) {
				array[i] = polygons.get(i);
			}
			output = new MultiPolygon(array);
		}

		else if (type.equalsIgnoreCase("GEOMETRYCOLLECTION")) {
			ArrayList<Geometry> geos = new ArrayList<>();
			Geometry geo = readFormattedGeoString("");
			geos.add(geo);
			nextToken = getNextCloserOrComma();
			while (nextToken.equals(COMMA)) {
				geo = readFormattedGeoString("");
				geos.add(geo);
				nextToken = getNextCloserOrComma();
			}
			Geometry[]array = new Geometry[geos.size()];
			for (int i = 0; i < geos.size(); i++) {
				array[i] = geos.get(i);
			}
			output = new GeometryCollection<Geometry>(array);
		}

		return output;

	}
	private String getNextWord() throws ParseException,
	IOException {
		int type = tokenizer.nextToken();
		switch (type) {
		case StreamTokenizer.TT_WORD:

			String word = tokenizer.sval;
			if (word.equalsIgnoreCase(EMPTY))
				return EMPTY;
			return word;

		case '(': return L_P;
		case ')': return R_P;
		case ',': return COMMA;
		}
		parseError("word");
		return null;
	}
	private void parseError(String expected)
			throws ParseException {
		String tokenStr = tokenString();
		throw new ParseException("Expected " + expected + " but found " + tokenStr, 0);
	}

	private String tokenString() {
		switch (tokenizer.ttype) {
		case StreamTokenizer.TT_NUMBER:
			return "<NUMBER>";
		case StreamTokenizer.TT_EOL:
			return "End-of-Line";
		case StreamTokenizer.TT_EOF:
			return "End-of-Stream";
		case StreamTokenizer.TT_WORD:
			return "'" + tokenizer.sval + "'";
		default:
		}
		return "'" + (char) tokenizer.ttype + "'";
	}
	private String getNextEmptyOrOpener() 
			throws IOException, ParseException {
		String nextWord = getNextWord();
		if (nextWord.equals(EMPTY) || nextWord.equals(L_P)) {
			return nextWord;
		}
		parseError(EMPTY + " or " + L_P);
		return null;
	}
	private double getNextNumber() 
			throws IOException, ParseException {
		int type = tokenizer.nextToken();
		switch (type) {
		case StreamTokenizer.TT_WORD:
		{
			try {
				return Double.parseDouble(tokenizer.sval);
			}
			catch (NumberFormatException ex) {
				throw new ParseException("Invalid number: " + tokenizer.sval,0);
			}
		}
		}
		parseError("number");
		return 0.0;
	}

	private String getNextCloser() 
			throws IOException, ParseException {
		String nextWord = getNextWord();
		if (nextWord.equals(R_P)) {
			return nextWord;
		}
		parseError(R_P);
		return null;
	}

	private String getNextCloserOrComma() 
			throws IOException, ParseException {
		String nextWord = getNextWord();
		if (nextWord.equals(COMMA) || nextWord.equals(R_P)) {
			return nextWord;
		}
		parseError(COMMA + " or " + R_P);
		return null;
	}
	
	private Coordinate getPreciseCoordinate()
			throws IOException, ParseException
	{
		Coordinate coord = new Coordinate();
		coord.setX(getNextNumber());
		coord.setY(getNextNumber());
		return coord;
	}

	private Coordinate[] getCoordinates() 
			throws IOException, ParseException {
		String nextToken ="";
		ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
		coordinates.add(getPreciseCoordinate());
		nextToken = getNextCloserOrComma();
		while (nextToken.equals(COMMA)) {
			coordinates.add(getPreciseCoordinate());
			nextToken = getNextCloserOrComma();
		}
		Coordinate[] array = new Coordinate[coordinates.size()];
		return (Coordinate[]) coordinates.toArray(array);
	}
}