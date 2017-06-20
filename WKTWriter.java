

import com.sinergise.geometry.Geometry;
import com.sinergise.geometry.GeometryCollection;
import com.sinergise.geometry.LineString;
import com.sinergise.geometry.MultiLineString;
import com.sinergise.geometry.MultiPoint;
import com.sinergise.geometry.MultiPolygon;
import com.sinergise.geometry.Point;
import com.sinergise.geometry.Polygon;

/******************************************************************************
  *  Author:       Deniz Ece Aktan
  *  Compilation:  javac WKTWriter.java
  *  Execution:    java WKTWriter
  * 
  *  Dependencies: com.sinergise.geometry 
  * 
  *  This class Transforms the input Geometry object into WKT-formatted String. e.g.
	 * new WKTWriter().write(new LineString(new double[]{30, 10, 10, 30, 40, 40}));
  *
  ******************************************************************************/

public class WKTWriter {

	/**
	 * Transforms the input Geometry object into WKT-formatted String. e.g.
	 * new WKTWriter().write(new LineString(new double[]{30, 10, 10, 30, 40, 40}));
	 */
	public String write(Geometry geom) {
		//TODO: Implement this
		String output = "";
		String className = geom.getClass().getSimpleName();
		output += className.toUpperCase()+ " ";
		if (geom.isEmpty())
			output += "EMPTY";
		else
			output = output + getFormattedStr(geom);       
		return output;
	}
	@SuppressWarnings("unchecked")
	private String getFormattedStr(Geometry geo)
	{
		String geoStrFormatted = "";
		if (geo instanceof LineString){
			LineString ls = (LineString)geo;
			geoStrFormatted +="(";
			for (int i = 0; i < ls.getNumCoords(); i++) {
				geoStrFormatted += fmt(ls.getX(i)) + " "
			+ fmt(ls.getY(i))
			+ (i != ls.getNumCoords() - 1?", ":"");
			}
			geoStrFormatted +=")";
		}
		else if (geo instanceof Point){
			Point p =(Point)geo;
			geoStrFormatted +="("+fmt(p.getX())+" " + fmt(p.getY())+")";
		}
		else if (geo instanceof Polygon) {
			Polygon pl = (Polygon)geo;
			geoStrFormatted += "(" + getFormattedStr(pl.getOuter());
			if (pl.getNumHoles() > 0) {
				geoStrFormatted += ",";
				for (int i = 0; i < pl.getNumHoles(); i++) {
					geoStrFormatted +=  
							 getFormattedStr(pl.getHole(i))+ 
							 ( i != pl.getNumHoles() - 1 ? ", " : "");
				}
			}
			geoStrFormatted += ")";
		}
		else if (geo instanceof MultiPoint){
			MultiPoint mp = (MultiPoint)geo;
			geoStrFormatted += "(";
			for (int i = 0; i < mp.size(); i++) {
				geoStrFormatted += getFormattedStr(mp.get(i))
						+ (i != mp.size()-1 ? ", " : "" );
			}
			geoStrFormatted += ")";

		}
		else if (geo instanceof MultiLineString){
			MultiLineString mls = (MultiLineString)geo;
			geoStrFormatted += "(";
			for (int i = 0; i<mls.size(); i++) {
				geoStrFormatted +=  getFormattedStr(mls.get(i))
						+ (i != mls.size()-1 ? ", " : "" );
			}
			geoStrFormatted += ")";
		}
		else if (geo instanceof MultiPolygon){
			MultiPolygon mlp = (MultiPolygon)geo;
			geoStrFormatted += "(";
			for (int i = 0; i<mlp.size(); i++) {
				geoStrFormatted +=  getFormattedStr(mlp.get(i))
						+ (i != mlp.size()-1 ? ", " : "" );
			}
			geoStrFormatted += ")";
		}
		else if (geo instanceof GeometryCollection){
			GeometryCollection<Geometry> gc =(GeometryCollection<Geometry>)geo;
			geoStrFormatted += "(";

			for (int i = 0; i < gc.size(); i++){
				geoStrFormatted += gc.get(i).getClass().getSimpleName().toUpperCase()
						+ " " + getFormattedStr(gc.get(i))
						+ (i != gc.size() - 1 ? ", " : "" );
			}
			geoStrFormatted += ")";
		}
		return geoStrFormatted;
	}
	private String fmt(double d) {
		if (d == (long) d)
			return String.format("%d",(long)d);
		else
			return String.format("%s",d);
	}
}