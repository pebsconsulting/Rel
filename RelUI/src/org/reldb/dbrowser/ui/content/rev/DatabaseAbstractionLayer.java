package org.reldb.dbrowser.ui.content.rev;

import java.io.IOException;

import javax.swing.JOptionPane;

import org.reldb.rel.client.Connection;
import org.reldb.rel.client.Error;
import org.reldb.rel.client.NullTuples;
import org.reldb.rel.client.Tuple;
import org.reldb.rel.client.Tuples;
import org.reldb.rel.client.Value;
import org.reldb.rel.client.Connection.HTMLReceiver;

public class DatabaseAbstractionLayer {

	public static final int EXPECTED_REV_VERSION = 0;
	public static final int QUERY_WAIT_MILLISECONDS = 5000;
	
	private synchronized static boolean execute(Connection connection, String query) {
		try {
			connection.execute(query);
			return true;
		} catch (IOException e1) {
			System.out.println("DatabaseAbstraction: Error: " + e1);
			e1.printStackTrace();
			return false;
		}
	}

	private synchronized static Tuples getTuples(Connection connection, String query) {
		Value response;
		try {
			response = connection.evaluate(query).awaitResult(QUERY_WAIT_MILLISECONDS);
		} catch (IOException e) {
			System.out.println("DatabaseAbstraction: Error: " + e);
			e.printStackTrace();
			return null;
		}
		if (response instanceof org.reldb.rel.client.Error) {
			System.out.println("DatabaseAbstraction: Query returns error. " + query + "\n");
			return null;
		}
		if (response == null) {
			System.out.println("DatabaseAbstraction: Unable to obtain query results.");
			return null;
		}
		return (Tuples)response;		
	}

	public synchronized static void evaluate(Connection connection, String query, HTMLReceiver htmlReceiver) {
		connection.evaluate(query, htmlReceiver);
	}

	public synchronized static Tuples evaluate(Connection connection, String query) {
		try {
			Value result = connection.evaluate(query).awaitResult(QUERY_WAIT_MILLISECONDS);
			if (result instanceof Error) {
				JOptionPane.showMessageDialog(null, result);
				return new NullTuples();
			}
			return (Tuples)result;
		} catch (IOException e) {
			e.printStackTrace();
			return new NullTuples();
		}
	}

	public synchronized static int hasRevExtensions(Connection connection) {
		String query = "sys.rev.Version";
		try {
			Value response = (Value)connection.evaluate(query).awaitResult(QUERY_WAIT_MILLISECONDS);
			if (response instanceof Tuples) {
				int version = -1;
				for (Tuple tuple: (Tuples)response)
					version = tuple.get("ver").toInt();
				return version;
			}
			return -1;
		} catch (IOException e) {
			System.out.println("Unable to obtain version of Rev extensions.  Are they not installed?");
			return -1;
		}
	}

	public synchronized static long getUniqueNumber(Connection connection) {
		String query = "GET_UNIQUE_NUMBER()";
		try {
			return connection.evaluate(query).awaitResult(QUERY_WAIT_MILLISECONDS).toLong();
		} catch (Exception e) {
			System.out.println("Rev failed to get a unique number: " + e);
			return 0;
		}
	}
	
	public static boolean installRevExtensions(Connection connection) {
		String query = 
				"var sys.rev.Version real relation {" +
				"	ver INTEGER" +
				"} INIT(relation {tuple {ver " + EXPECTED_REV_VERSION + "}}) key {ver};" +
				
			    "var sys.rev.Relvar real relation {" +
			    "	    Name CHAR, " +
			    " relvarName CHAR, " +
			    "	    xpos INTEGER, " +
			    "	    ypos INTEGER, " +
			    "	   model CHAR" +
			    "} key {Name};" +
			    
			    "var sys.rev.Query real relation {" +
			    "   Name CHAR, " +
			    "   xpos INTEGER, " +
			    "   ypos INTEGER, " +
			    "   kind CHAR, " +
			    "   connections RELATION {" +
			    "      parameter INTEGER, " +
			    "      Name CHAR" +
			    "   }," +
			    "	 model CHAR" +
			    "} key {Name};" +
			    
			    "var sys.rev.View real relation {" +
			    "   Name CHAR, " +
			    "   xpos INTEGER, " +
			    "   ypos INTEGER, " +
			    "	width INTEGER, " +
			    "	height INTEGER, " +
			    "	enabled BOOLEAN, " +
			    "	stored BOOLEAN" +
			    "} key {Name};" +
			    
			    "var sys.rev.Op_Project real relation {" +
				"   Name CHAR, " +
			    "   Definition CHAR" +
				"} key {Name};" +
			    
				"var sys.rev.Op_Restrict real relation {" +
				"   Name CHAR, " +
				"   Definition CHAR" +
				"} key {Name};" +
				
				"var sys.rev.Op_Rename real relation {" +
				"   Name CHAR, " +
				"   Definition CHAR" +
				"} key {Name};" +
				
				"var sys.rev.Op_Order real relation {" +
				"   Name CHAR, " +
			    "	Relvar CHAR, " +
				"   selections RELATION {" +
				"	ID INTEGER" +
				"   , attribute CHAR" +
				"	, selected BOOLEAN" +
				"	, SortType INTEGER" +
				"   }" +
				"} key {Name};" +
				
	    		"var sys.rev.Op_Group real relation {" +
	    		"   Name CHAR, " +
			    "	Relvar CHAR, " +
	    		"   AllBut BOOLEAN, " +
	    		"   selections RELATION {" +
	    		"      attribute CHAR" +
	    		"   }" +
	    		"	, ASText CHAR" +
	    		"} key {Name};" +
	    		
				"var sys.rev.Op_Ungroup real relation {" +
				"   Name CHAR, " +
			    "	Relvar CHAR, " +
				"   selections RELATION {" +
				"      attribute CHAR" +
				"   }" +
				"} key {Name};" +
				
	    		"var sys.rev.Op_Wrap real relation {" +
	    		"   Name CHAR, " +
			    "	Relvar CHAR, " +
	    		"   AllBut BOOLEAN, " +
	    		"   selections RELATION {" +
	    		"      attribute CHAR" +
	    		"   }" +
	    		"	, ASText CHAR" +
	    		"} key {Name};" +
	    		
				"var sys.rev.Op_Unwrap real relation {" +
				"   Name CHAR, " +
			    "	Relvar CHAR, " +
				"   selections RELATION {" +
				"      attribute CHAR" +
				"   }" +
				"} key {Name};" +
				
				"var sys.rev.Op_Extend real relation {" +
				"   Name CHAR, " +
				"   Definition RELATION {" +
				"	  ID INTEGER," +
				"     attribute CHAR," +
				"	  expression CHAR" +
				"   }" +
				"} key {Name};" +

				"var sys.rev.Op_Summarize real relation {" +
				"   Name CHAR, " +
			    "	Relvar CHAR, " +
				"   subRelvar RELATION {" +
				"	ID INTEGER" +
				"   , attribute CHAR" +
				"	, expression CHAR" +
				"   }" +
				"} key {Name};" ;
		
		return execute(connection, query);
	}

	public static boolean removeRevExtensions(Connection connection) {
		String query = 
				"drop var sys.rev.Op_Restrict;" +
				"drop var sys.rev.Op_Rename;" +
				"drop var sys.rev.Op_Project;" +
				"drop var sys.rev.Op_Order;" +
				"drop var sys.rev.Op_Group;" +
				"drop var sys.rev.Op_Ungroup;" +
				"drop var sys.rev.Op_Wrap;" +
				"drop var sys.rev.Op_Unwrap;" +
				"drop var sys.rev.Op_Extend;" +
				"drop var sys.rev.Op_Summarize;" +
			    "drop var sys.rev.Query;" +
				"drop var sys.rev.Relvar;" +
			    "drop var sys.rev.View;" +
				"drop var sys.rev.Version;";
		return execute(connection, query);
	}

	public static Tuples getRelvarsWithoutRevExtensions(Connection connection) {
		return getRelvarsWithoutRevExtensions(connection, "");
	}
	
	public static Tuples getRelvarsWithoutRevExtensions(Connection connection, String where) {
		String query = "sys.Catalog {Name, Owner} WHERE " + where;
		return getTuples(connection, query);
	}
	
	public static Tuples getRelvarsWithRevExtensions(Connection connection) {
		String query = 
				"union {" + 
				"   sys.rev.Relvar," +
				"   extend sys.Catalog not matching sys.rev.Relvar : {xpos := -1, ypos := -1} {Name, xpos, ypos}" +
				"} matching sys.Catalog";
		return getTuples(connection, query);
	}
	
	public static Tuples getRelvars(Connection connection, String where) {
		String query = "sys.rev.Relvar";
		if (where.length() > 0) {
			query += " WHERE " + where;
		}
		return getTuples(connection, query);
	}

	public static Tuples getQueries(Connection connection, String where) {
		String query = "sys.rev.Query";
		if (where.length() > 0) {
			query += " WHERE " + where;
		}
		return getTuples(connection, query);
	}
	
	public static Tuples getViews(Connection connection, String where) {
		String query = "sys.rev.View";
		if (where.length() > 0) {
			query += " WHERE " + where;
		}
		return getTuples(connection, query);
	}
	
	// Update relvar position
	public static void updateRelvarPosition(Connection connection, String name, String relvarName, int x, int y, String model) {
		String query = 
				"DELETE sys.rev.Relvar where Name='" + name + "', " + 
                "INSERT sys.rev.Relvar relation {tuple {Name '" + name + "', relvarName '" + relvarName + "', xpos " + x + ", ypos " + y + ", model '" + model + "'}};";
		execute(connection, query);
	}
	
	// Update query operator position
	public static void updateQueryPosition(Connection connection, String name, int x, int y, String kind, String connections, String model) {
		String query = 
				"DELETE sys.rev.Query where Name='" + name + "', " + 
                "INSERT sys.rev.Query relation {tuple {" +
						"Name '" + name + "', " +
						"xpos " + x + ", " +
						"ypos " + y + ", " +
						"kind '" + kind + "', " + 
						"connections " + connections + 
						", model '" + model + "'" +
					"}};";
		execute(connection, query);
	}
	
	// Update view position
	public static void updateViewPosition(Connection connection, String name, int x, int y, int width, int height, boolean enabled, boolean stored) {
		String query = 
				"DELETE sys.rev.View where Name='" + name + "', " + 
                "INSERT sys.rev.View relation {tuple {" +
						"Name '" + name + "', " +
						"xpos " + x + ", " +
						"ypos " + y + ", " +
						"width " + width + ", " +
						"height " + height + ", " +
						"enabled " + enabled + ", " +
						"stored " + stored +
					"}};";
		execute(connection, query);
	}

	//Preserved States
	
	//Views
	public static Tuples getPreservedView(Connection connection, String name) {
		String query = "sys.rev.View WHERE Name = '" + name + "'";
		return getTuples(connection, query);
	}
	
	//Project
	public static Tuples getPreservedStateProject(Connection connection, String name) {
		String query = "sys.rev.Op_Project WHERE Name = '" + name + "'";
		return getTuples(connection, query);
	}
	
	public static void updatePreservedStateProject(Connection connection, String name, String definition) {
		String query = "DELETE sys.rev.Op_Project WHERE Name = '" + name + "', " +
		               "INSERT sys.rev.Op_Project RELATION {" +
		                  "TUPLE {Name '" + name + "', Definition '" + definition + "'}" +
		               "};";
		execute(connection, query);
	}
	
	// Restrict
	public static Tuples getPreservedStateRestrict(Connection connection, String name) {
		String query = "sys.rev.Op_Restrict WHERE Name = '" + name + "'";
		return getTuples(connection, query);
	}
	
	public static void updatePreservedStateRestrict(Connection connection, String name, String definition) {
		String query = "DELETE sys.rev.Op_Restrict WHERE Name = '" + name + "', " +
                       "INSERT sys.rev.Op_Restrict RELATION {" +
                       "  TUPLE {Name '" + name + "', Definition '" + definition + "'}" +
                       "};";
		execute(connection, query);
	}
	
	// Rename
	public static Tuples getPreservedStateRename(Connection connection, String name) {
		String query = "sys.rev.Op_Rename WHERE Name = '" + name + "'";
		return getTuples(connection, query);
	}
	
	public static void updatePreservedStateRename(Connection connection, String name, String definition) {
		String query = "DELETE sys.rev.Op_Rename WHERE Name = '" + name + "', " +
		               "INSERT sys.rev.Op_Rename RELATION {" +
		               "  TUPLE {Name '" + name + "', Definition '" + definition + "'}" +
		               "};";
		execute(connection, query);
	}
	
	// Order
	public static Tuples getPreservedStateOrder(Connection connection, String name) {
		String query = "sys.rev.Op_Order WHERE Name = '" + name + "'";
		return getTuples(connection, query);
	}
	
	public static void updatePreservedStateOrder(Connection connection, String name, String relvar, String selections) {
		String query = "DELETE sys.rev.Op_Order WHERE Name = '" + name + "', " +
		               "INSERT sys.rev.Op_Order RELATION {" +
		               "  TUPLE {Name '" + name + "', Relvar '" + relvar + "', " + selections + "}};";
		execute(connection, query);
	}
	
	// Group
	public static Tuples getPreservedStateGroup(Connection connection, String name) {
		String query = "sys.rev.Op_Group WHERE Name = '" + name + "'";
		return getTuples(connection, query);
	}
	
	public static void updatePreservedStateGroup(Connection connection, String name, String relvar, String allBut, String selections, String as) {
		String query = "DELETE sys.rev.Op_Group WHERE Name = '" + name + "', " +
		               "INSERT sys.rev.Op_Group RELATION {" +
		               "  TUPLE {Name '" + name + "', Relvar '" + relvar + "', " + allBut + ", " + selections + ", ASText '" + as + "'}};";
		execute(connection, query);
	}
	
	// Ungroup
	public static Tuples getPreservedStateUngroup(Connection connection, String name) {
		String query = "sys.rev.Op_Ungroup WHERE Name = '" + name + "'";
		return getTuples(connection, query);
	}
	
	public static void updatePreservedStateUngroup(Connection connection, String name, String relvar, String selections) {
		String query = "DELETE sys.rev.Op_Ungroup WHERE Name = '" + name + "', " +
		               "INSERT sys.rev.Op_Ungroup RELATION {" +
		               "  TUPLE {Name '" + name + "', Relvar '" + relvar + "', " + selections + "}};";
		execute(connection, query);
	}
	
	// Wrap
	public static Tuples getPreservedStateWrap(Connection connection, String name) {
		String query = "sys.rev.Op_Wrap WHERE Name = '" + name + "'";
		return getTuples(connection, query);
	}
	
	public static void updatePreservedStateWrap(Connection connection, String name, String relvar, String allBut, String selections, String as) {
		String query = "DELETE sys.rev.Op_Wrap WHERE Name = '" + name + "', " +
		               "INSERT sys.rev.Op_Wrap RELATION {" +
		               "  TUPLE {Name '" + name + "', Relvar '" + relvar + "', " + allBut + ", " + selections + ", ASText '" + as + "'}};";
		execute(connection, query);
	}
	
	// Unwrap
	public static Tuples getPreservedStateUnwrap(Connection connection, String name) {
		String query = "sys.rev.Op_Unwrap WHERE Name = '" + name + "'";
		return getTuples(connection, query);
	}
	
	public static void updatePreservedStateUnwrap(Connection connection, String name, String relvar, String selections) {
		String query = "DELETE sys.rev.Op_Unwrap WHERE Name = '" + name + "', " +
		               "INSERT sys.rev.Op_Unwrap RELATION {" +
		               "  TUPLE {Name '" + name + "', Relvar '" + relvar + "', " + selections + "}};";
		execute(connection, query);
	}
	
	// Extend
	public static Tuples getPreservedStateExtend(Connection connection, String name) {
		String query = "(sys.rev.Op_Extend WHERE Name = '" + name + "') UNGROUP Definition ORDER(ASC ID)";
		return getTuples(connection, query);
	}
	
	public static void updatePreservedStateExtend(Connection connection, String name, String definition) {
		String query = "DELETE sys.rev.Op_Extend WHERE Name = '" + name + "', " +
		               "INSERT sys.rev.Op_Extend RELATION {" +
		               "  TUPLE {Name '" + name + "', Definition " + definition + 
		               "}};";
		System.out.println("DatabaseAbstractionLayer: EXTEND: " + query);
		execute(connection, query);
	}
	
	//Summarize
	public static Tuples getPreservedStateSummarize(Connection connection, String name) {
		String query = "sys.rev.Op_Summarize WHERE Name = '" + name + "'";
		return getTuples(connection, query);
	}
	
	public static void updatePreservedStateSummarize(Connection connection, String name, String relvar, String subRelvar) {
		String query = "DELETE sys.rev.Op_Summarize WHERE Name = '" + name + "', " +
		               "INSERT sys.rev.Op_Summarize RELATION {" +
		               "  TUPLE {Name '" + name + "', Relvar '" + relvar + "', " + subRelvar + "}};";
		execute(connection, query);
	}

	public static void executeHandler(Connection connection, String query) {
		execute(connection, query);
	}
	
	public static void removeOperator(Connection connection, String name) {
		String query = "DELETE sys.rev.Query WHERE Name = '" + name + "';"; 
		execute(connection, query);
	}
	
	public static void removeRelvar(Connection connection, String name) {
		String query = "DELETE sys.rev.Relvar WHERE Name = '" + name + "';"; 
		execute(connection, query);
	}
	
	public static void removeView(Connection connection, String name) {
		String query = "DELETE sys.rev.View WHERE Name = '" + name + "';";
		execute(connection, query);
	}
	
	public static void removeOperator_Project(Connection connection, String name) {
		String query = "DELETE sys.rev.Op_Project WHERE Name = '" + name + "';";
		execute(connection, query);
	}
	
	public static void removeOperator_Restrict(Connection connection, String name) {
		String query = "DELETE sys.rev.Op_Restrict WHERE Name = '" + name + "';";
		execute(connection, query);
	}
	
	public static void removeOperator_Rename(Connection connection, String name) {
		String query = "DELETE sys.rev.Op_Rename WHERE Name = '" + name + "';";
		execute(connection, query);
	}
	
	public static void removeOperator_Extend(Connection connection, String name) {
		String query = "DELETE sys.rev.Op_Extend WHERE Name = '" + name + "';";
		execute(connection, query);
	}
	
	public static void removeOperator_PRODUCT(Connection connection, String name) {
		String query = "DELETE sys.rev.Op_PRODUCT WHERE Name = '" + name + "';";
		execute(connection, query);
	}
	
	public static void removeOperator_DIVIDEBY(Connection connection, String name) {
		String query = "DELETE sys.rev.Op_DIVIDEBY WHERE Name = '" + name + "';";
		execute(connection, query);
	}
		
	public static void removeOperator_Order(Connection connection, String name) {
		String query = "DELETE sys.rev.Op_Order WHERE Name = '" + name + "';";
		execute(connection, query);
	}
	
	public static void removeOperator_Group(Connection connection, String name) {
		String query = "DELETE sys.rev.Op_Group WHERE Name = '" + name + "';";
		execute(connection, query);
	}
	
	public static void removeOperator_Ungroup(Connection connection, String name) {
		String query = "DELETE sys.rev.Op_Ungroup WHERE Name = '" + name + "';";
		execute(connection, query);
	}
	
	public static void removeOperator_Wrap(Connection connection, String name) {
		String query = "DELETE sys.rev.Op_Wrap WHERE Name = '" + name + "';";
		execute(connection, query);
	}
	
	public static void removeOperator_Unwrap(Connection connection, String name) {
		String query = "DELETE sys.rev.Op_Unwrap WHERE Name = '" + name + "';";
		execute(connection, query);
	}
	
	public static void removeOperator_Summarize(Connection connection, String name) {
		String query = "DELETE sys.rev.Op_Summarize WHERE Name = '" + name + "';";
		execute(connection, query);
	}
}
