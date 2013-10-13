/* 
 * Helen Liu
 * 30245078
 * hliu14@cmc.edu
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.*;
import java.util.*;
import java.lang.*;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;


public class DBMS {

	public String name;
	public ArrayList<Table> tables;

	// empty default constructor
	public DBMS() {
	}

	// constructor for CREATE DATABASE commands
	public DBMS(String name) {
		this.name = name;
		this.tables = new ArrayList<Table>();
	}

	public String getDbName() {
		return name;
	}

	public ArrayList<Table> getDbTables() {
		return tables;
	}


	/*******************************************************
	 *   PROCESS CREATE TABLE STATEMENT
	 *******************************************************/

	public static void createTable(CreateTableStatement stmt) throws IOException {
		String name = stmt.getName();
		ArrayList<String> attrNames = stmt.getAttrNames();
		ArrayList<String> attrTypes = stmt.getAttrTypes();



		CreateTable.writeTableClass(name, attrNames, attrTypes);


		//COPY AND PASTED FROM MAIN OF CREATETABLE.JAVA
		String classToCreate = name;
		String fileToCompile = ".//src/" + name + ".java";
		//String fileToCompile = name + ".java";

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		int compilationResult = compiler.run(null, null, null, "-d", ".//bin/", fileToCompile);
		//int compilationResult = compiler.run(null, null, null, fileToCompile);

		if (compilationResult == 0) {
			System.out.println("Compilation is successful: Table " + stmt.getName() + " created");
		} else {
			System.out.println("Compilation Failed");
		}
		//END
	}

	/********************************************************
	 *  PROCESS SELECT STATEMENT
	 *******************************************************/

	public static void select(SelectStatement stmt, DBMS currentDB, PrintStream printStream) { // called from select.java
		if (currentDB != null) {
			
			Table table = null;
			
			Table table1 = getTable(currentDB, stmt.getFrom().get(0), printStream);
			Table table2 = null;
			if (stmt.getFrom().size() == 2) {
				table2 = getTable(currentDB, stmt.getFrom().get(1), printStream);
			}
			
			
			Boolean joinedTables = false;
			Table filteredTable = null;
			
			if (table2 == null) { // if table2 does not exist
				table = table1;
				filteredTable = table1;
			} else {

				// if where clause contains only attr op val clauses
					// create a cross-product of the two tables (like before)
				// else if where clause contains an attr op attr clause
					// create an index and perform an index nested loops join
				
				Boolean attrOpAttr = false;
				int index = -1;
				
				for (int i = 0; i < stmt.getWhere().size(); i++) {
					EqualExpression where = stmt.getWhere().get(i);
					String attr2Name = where.getAttr2Name();
					if (attr2Name != null) {
						attrOpAttr = true;
						index = i;
						break;
					}
				}
				
				if (attrOpAttr == false) { // if where clause contains only attr op val clauses
					
					// create a cross-product of the two tables
					table = joinTables(table1, table2);
					joinedTables = true;
					
					// For selection part of query, save resulting table as intermediate result
					filteredTable = table;
					int whereSize = stmt.getWhere().size();
					for (int i = 0; i < whereSize; i++) {
						EqualExpression where = stmt.getWhere().get(i);
						filteredTable = filterBySelectionConditions(filteredTable, where, joinedTables, table1, table2);
						/**ON FIRST CALL OF THE ABOVE, FILTEREDTABLE = TABLE**/
					}
				
				} else { // if where clause contains an attr op attr clause (there must be two tables in FROM clause in this case)
					
					
					/***********************************************************************
					 * 						SORT-MERGE JOIN
					 * 
					 **********************************************************************/
					EqualExpression where = stmt.getWhere().get(index);
					String op = where.getOperator();
					String sortByAttr1 = where.getAttrName();
					String sortByAttr2 = where.getAttr2Name();
					
					// Sort tables by appropriate attributes
					sortTable(table1, sortByAttr1);
					
					sortTable(table2, sortByAttr2);
					
					ArrayList<Object> table1Tuples = table1.getTuples();
					ArrayList<Object> table2Tuples = table2.getTuples();
					
					ArrayList<Object> joinedTableTuples = new ArrayList<Object>();
					
					try {
						
						Class classToCheck1 = Class.forName(table1.getTableName());
						Class classToCheck2 = Class.forName(table2.getTableName());
						
						String getMethod1Name = "get" + sortByAttr1;
						String getMethod2Name = "get" + sortByAttr2;
						
						Method getMethod1 = classToCheck1.getMethod(getMethod1Name);
						Method getMethod2 = classToCheck2.getMethod(getMethod2Name);
						
						Object table1Tuple = null;
						Object table2Tuple = null;
						Object table2Partition = table2Tuples.get(0);
						
						int table1Count = 0;
						int table2PartitionCount = 0;
						int table2Count = 0;
					
						int comp;
						String attr1Val = "";
						String attr2Val = "";
						
						table1Tuple = table1Tuples.get(table1Count);
						table2Partition = table2Tuples.get(table2PartitionCount);
						
						attr1Val = getMethod1.invoke(table1Tuple).toString();
						attr2Val = getMethod2.invoke(table2Partition).toString();
						
			        	try {
			        		comp = Integer.parseInt(attr1Val) - Integer.parseInt(attr2Val);
			        	} catch (NumberFormatException e) {
			        		comp = ((Comparable)attr1Val).compareTo(attr2Val);
			        	}
						
						while (table1Count < table1Tuples.size() && table2PartitionCount < table2Tuples.size()) {
				        	
				        	if (op.equals("=")) {
					        	while (comp < 0) {   		
					        		// continue scan of table1
					        		table1Count++;
					        		
					        		if (table1Count < table1Tuples.size()) {
						        		table1Tuple = table1Tuples.get(table1Count);
										table2Partition = table2Tuples.get(table2PartitionCount);
						        		
										attr1Val = getMethod1.invoke(table1Tuple).toString();
										attr2Val = getMethod2.invoke(table2Partition).toString();
										
							        	try {
							        		comp = Integer.parseInt(attr1Val) - Integer.parseInt(attr2Val);
							        	} catch (NumberFormatException e) {
							        		comp = ((Comparable)attr1Val).compareTo(attr2Val);
							        	}
					        		} else {
					        			break;
					        		}
					        	
					        	}
					        	
					        	while (comp > 0) {
					        		
					        		// continue scan of table2
					        		table2PartitionCount++;
					        		
					        		if (table2PartitionCount < table2Tuples.size()) {
					        		
						        		table1Tuple = table1Tuples.get(table1Count);
										table2Partition = table2Tuples.get(table2PartitionCount);
						        		
										attr1Val = getMethod1.invoke(table1Tuple).toString();
										attr2Val = getMethod2.invoke(table2Partition).toString();
										
							        	try {
							        		comp = Integer.parseInt(attr1Val) - Integer.parseInt(attr2Val);
							        	} catch (NumberFormatException e) {
							        		comp = ((Comparable)attr1Val).compareTo(attr2Val);
							        	}
					        		} else {
					        			break;
					        		}
					        	}
					        	
					        	table2Tuple = table2Partition; // in case table1Tuple != table2Tuple in any case
					        	table2Count = table2PartitionCount;
					        	
					        	while (comp == 0) {
					        		
					        		if (table1Count < table1Tuples.size() && table2Count < table2Tuples.size()) {
					        		
						        		table2Tuple = table2Partition;
						        		
						        		table1Tuple = table1Tuples.get(table1Count);
										table2Tuple = table2Tuples.get(table2Count);
						        		
										attr1Val = getMethod1.invoke(table1Tuple).toString();
										attr2Val = getMethod2.invoke(table2Tuple).toString();
										
							        	try {
							        		comp = Integer.parseInt(attr1Val) - Integer.parseInt(attr2Val);
							        	} catch (NumberFormatException e) {
							        		comp = ((Comparable)attr1Val).compareTo(attr2Val);
							        	}
					        		} else {
					        			break;
					        		}
					        		
						        	while (comp == 0) {
						        		ArrayList<Object> singleJoinedTuple = new ArrayList<Object>();
						        		singleJoinedTuple.add(table1Tuple);
						        		singleJoinedTuple.add(table2Tuple);
						        		joinedTableTuples.add(singleJoinedTuple); // add to result
						        		
						        		table2Count++; // advance table 2 scan
						        		
						        		if (table2Count < table2Tuples.size()) {
						        		
							        		table1Tuple = table1Tuples.get(table1Count);
											table2Tuple = table2Tuples.get(table2Count);
							        		
											attr1Val = getMethod1.invoke(table1Tuple).toString();
											attr2Val = getMethod2.invoke(table2Tuple).toString();
											
								        	try {
								        		comp = Integer.parseInt(attr1Val) - Integer.parseInt(attr2Val);
								        	} catch (NumberFormatException e) {
								        		comp = ((Comparable)attr1Val).compareTo(attr2Val);
								        	}
						        		} else {
						        			break;
						        		}
						        		
						        	}
						        	
						        	table1Count++; // advance table 1 scan
						        	
					        		if (table1Count < table1Tuples.size() && table2Count < table2Tuples.size()) {
						        	
							        	table1Tuple = table1Tuples.get(table1Count);
										table2Tuple = table2Tuples.get(table2Count);
						        		
										attr1Val = getMethod1.invoke(table1Tuple).toString();
										attr2Val = getMethod2.invoke(table2Tuple).toString();
										
							        	try {
							        		comp = Integer.parseInt(attr1Val) - Integer.parseInt(attr2Val);
							        	} catch (NumberFormatException e) {
							        		comp = ((Comparable)attr1Val).compareTo(attr2Val);
							        	}
					        		} else {
					        			break;
					        		}
						        	
						        	
					        	}
					        	
					        	table2Partition = table2Tuple;
					        	table2PartitionCount = table2Count;
				        	} else if (op.equals("<")) {
					        	while (comp == 0) {   		
					        		// continue scan of table2
					        		table2PartitionCount++;
					        		
					        		if (table2PartitionCount < table2Tuples.size()) {
					        		
						        		table1Tuple = table1Tuples.get(table1Count);
										table2Partition = table2Tuples.get(table2PartitionCount);
						        		
										attr1Val = getMethod1.invoke(table1Tuple).toString();
										attr2Val = getMethod2.invoke(table2Partition).toString();
										
							        	try {
							        		comp = Integer.parseInt(attr1Val) - Integer.parseInt(attr2Val);
							        	} catch (NumberFormatException e) {
							        		comp = ((Comparable)attr1Val).compareTo(attr2Val);
							        	}
					        		} else {
					        			break;
					        		}
					        	
					        	}
					        	
					        	while (comp > 0) {
					        		
					        		// continue scan of table2
					        		table2PartitionCount++;
					        		
					        		if (table2PartitionCount < table2Tuples.size()) {
					        		
						        		table1Tuple = table1Tuples.get(table1Count);
										table2Partition = table2Tuples.get(table2PartitionCount);
						        		
										attr1Val = getMethod1.invoke(table1Tuple).toString();
										attr2Val = getMethod2.invoke(table2Partition).toString();
										
							        	try {
							        		comp = Integer.parseInt(attr1Val) - Integer.parseInt(attr2Val);
							        	} catch (NumberFormatException e) {
							        		comp = ((Comparable)attr1Val).compareTo(attr2Val);
							        	}
					        		} else {
					        			break;
					        		}
					        	}
					        	
					        	table2Tuple = table2Partition; // in case table1Tuple != table2Tuple in any case
					        	table2Count = table2PartitionCount;
					        	
					        	while (comp < 0) {
					        		
					        		if (table1Count < table1Tuples.size() && table2Count < table2Tuples.size()) {
					        		
						        		table2Tuple = table2Partition;
						        		
						        		table1Tuple = table1Tuples.get(table1Count);
										table2Tuple = table2Tuples.get(table2Count);
						        		
										attr1Val = getMethod1.invoke(table1Tuple).toString();
										attr2Val = getMethod2.invoke(table2Tuple).toString();
										
							        	try {
							        		comp = Integer.parseInt(attr1Val) - Integer.parseInt(attr2Val);
							        	} catch (NumberFormatException e) {
							        		comp = ((Comparable)attr1Val).compareTo(attr2Val);
							        	}
					        		} else {
					        			break;
					        		}
					        		
						        	while (comp < 0) {
						        		ArrayList<Object> singleJoinedTuple = new ArrayList<Object>();
						        		singleJoinedTuple.add(table1Tuple);
						        		singleJoinedTuple.add(table2Tuple);
						        		joinedTableTuples.add(singleJoinedTuple); // add to result
						        		
						        		table2Count++; // advance table 2 scan
						        		
						        		if (table2Count < table2Tuples.size()) {
						        		
							        		table1Tuple = table1Tuples.get(table1Count);
											table2Tuple = table2Tuples.get(table2Count);
							        		
											attr1Val = getMethod1.invoke(table1Tuple).toString();
											attr2Val = getMethod2.invoke(table2Tuple).toString();
											
								        	try {
								        		comp = Integer.parseInt(attr1Val) - Integer.parseInt(attr2Val);
								        	} catch (NumberFormatException e) {
								        		comp = ((Comparable)attr1Val).compareTo(attr2Val);
								        	}
						        		} else {
						        			break;
						        		}
						        		
						        	}
						        	
						        	table1Count++; // advance table 1 scan
						        	table2PartitionCount = 0; //????
						        	table2Count = 0; // ????
						        	
					        		if (table1Count < table1Tuples.size() && table2Count < table2Tuples.size()) {
						        	
							        	table1Tuple = table1Tuples.get(table1Count);
										table2Tuple = table2Tuples.get(table2Count);
						        		
										attr1Val = getMethod1.invoke(table1Tuple).toString();
										attr2Val = getMethod2.invoke(table2Tuple).toString();
										
							        	try {
							        		comp = Integer.parseInt(attr1Val) - Integer.parseInt(attr2Val);
							        	} catch (NumberFormatException e) {
							        		comp = ((Comparable)attr1Val).compareTo(attr2Val);
							        	}
					        		} else {
					        			break;
					        		}
						        	
						        	
					        	}
					        	
					        	table2Partition = table2Tuple;
					        	table2PartitionCount = table2Count;
				        	} else {
					        	while (comp < 0) {   		
					        		// continue scan of table1
					        		table1Count++;
					        		
					        		if (table1Count < table1Tuples.size()) {
						        		table1Tuple = table1Tuples.get(table1Count);
										table2Partition = table2Tuples.get(table2PartitionCount);
						        		
										attr1Val = getMethod1.invoke(table1Tuple).toString();
										attr2Val = getMethod2.invoke(table2Partition).toString();
										
							        	try {
							        		comp = Integer.parseInt(attr1Val) - Integer.parseInt(attr2Val);
							        	} catch (NumberFormatException e) {
							        		comp = ((Comparable)attr1Val).compareTo(attr2Val);
							        	}
					        		} else {
					        			break;
					        		}
					        	
					        	}
					        	
					        	while (comp == 0) {
					        		
					        		// continue scan of table1
					        		table1Count++;
					        		
					        		if (table1Count < table1Tuples.size()) {
						        		table1Tuple = table1Tuples.get(table1Count);
										table2Partition = table2Tuples.get(table2PartitionCount);
						        		
										attr1Val = getMethod1.invoke(table1Tuple).toString();
										attr2Val = getMethod2.invoke(table2Partition).toString();
										
							        	try {
							        		comp = Integer.parseInt(attr1Val) - Integer.parseInt(attr2Val);
							        	} catch (NumberFormatException e) {
							        		comp = ((Comparable)attr1Val).compareTo(attr2Val);
							        	}
					        		} else {
					        			break;
					        		}
					        	}
					        	
					        	table2Tuple = table2Partition; // in case table1Tuple != table2Tuple in any case
					        	table2Count = table2PartitionCount;
					        	
					        	while (comp > 0) {
					        		
					        		if (table1Count < table1Tuples.size() && table2Count < table2Tuples.size()) {
					        		
						        		table2Tuple = table2Partition;
						        		
						        		table2Count = 0; // ????
						        		
						        		table1Tuple = table1Tuples.get(table1Count);
										table2Tuple = table2Tuples.get(table2Count);
						        		
										attr1Val = getMethod1.invoke(table1Tuple).toString();
										attr2Val = getMethod2.invoke(table2Tuple).toString();
										
							        	try {
							        		comp = Integer.parseInt(attr1Val) - Integer.parseInt(attr2Val);
							        	} catch (NumberFormatException e) {
							        		comp = ((Comparable)attr1Val).compareTo(attr2Val);
							        	}
					        		} else {
					        			break;
					        		}
					        		
						        	while (comp > 0) {
						        		ArrayList<Object> singleJoinedTuple = new ArrayList<Object>();
						        		singleJoinedTuple.add(table1Tuple);
						        		singleJoinedTuple.add(table2Tuple);
						        		joinedTableTuples.add(singleJoinedTuple); // add to result
						        		
						        		table2Count++; // advance table 2 scan
						        		
						        		if (table2Count < table2Tuples.size()) {
						        		
							        		table1Tuple = table1Tuples.get(table1Count);
											table2Tuple = table2Tuples.get(table2Count);
							        		
											attr1Val = getMethod1.invoke(table1Tuple).toString();
											attr2Val = getMethod2.invoke(table2Tuple).toString();
											
								        	try {
								        		comp = Integer.parseInt(attr1Val) - Integer.parseInt(attr2Val);
								        	} catch (NumberFormatException e) {
								        		comp = ((Comparable)attr1Val).compareTo(attr2Val);
								        	}
						        		} else {
						        			break;
						        		}
						        		
						        	}
						        	
						        	table1Count++; // advance table 1 scan
						        	
					        		if (table1Count < table1Tuples.size() && table2Count < table2Tuples.size()) {
						        	
							        	table1Tuple = table1Tuples.get(table1Count);
										table2Tuple = table2Tuples.get(table2Count);
						        		
										attr1Val = getMethod1.invoke(table1Tuple).toString();
										attr2Val = getMethod2.invoke(table2Tuple).toString();
										
							        	try {
							        		comp = Integer.parseInt(attr1Val) - Integer.parseInt(attr2Val);
							        	} catch (NumberFormatException e) {
							        		comp = ((Comparable)attr1Val).compareTo(attr2Val);
							        	}
					        		} else {
					        			break;
					        		}
						        	
						        	
					        	}
					        	
					        	table2Partition = table2Tuple;
					        	table2PartitionCount = table2Count;
				        	}
						}
						
						// Joined table name
						String joinedTableName = table1.getTableName() + "-JOIN-" + table2.getTableName();
						
						// Joined table attribute names
						ArrayList<String> joinedAttrNames = new ArrayList<String>();
						joinedAttrNames.addAll(table1.getAttrNames());
						joinedAttrNames.addAll(table2.getAttrNames());
						
						// Joined table attribute types
						ArrayList<String> joinedAttrTypes = new ArrayList<String>();
						joinedAttrTypes.addAll(table1.getAttrTypes());
						joinedAttrTypes.addAll(table2.getAttrTypes());
						
						filteredTable = new Table(joinedTableName, joinedAttrNames, joinedAttrTypes, joinedTableTuples);
						
						joinedTables = true;
						
					}
					catch (ClassNotFoundException e) {
						System.out.println("ClassNotFoundException2");
						System.out.println(e.getMessage());
					}
					catch (IllegalAccessException e) {
						System.out.println("ccc");
						System.out.println(e.getMessage());
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (Exception e) {
						// Some other exception
						e.printStackTrace();
					}	
					
					
					
					
					
					
					/***********************************************************************
					 * 							INDEX JOIN
					 * 
					 ***********************************************************************/
					
					
					
					// create an index on the second attr by first sorting data entries by search key,
					// then inserting them into the index tree
					
					// WE WILL, BY DEFAULT, INDEX ATTR2NAME OF THE WHERE CLAUSE
					
//					EqualExpression where = stmt.getWhere().get(index);
//					String attrToIndex = where.getAttr2Name();
//					Table tableToSort = null;
//					
//					// find out which table we need to sort
//					for (int i = 0; i < table1.getAttrNames().size(); i++) {
//						if (table1.getAttrNames().get(i).equals(attrToIndex)) {
//							tableToSort = table1;
//							break;
//						}
//					}
//					
//					for (int i = 0; i < table2.getAttrNames().size(); i++) {
//						if (tableToSort == table1) {
//							break;
//						}
//						if (table2.getAttrNames().get(i).equals(attrToIndex)) {
//							tableToSort = table2;
//							break;
//						}
//					}
//					
//					// sort the table
//					ArrayList<Object> tuplesToSort = tableToSort.getTuples();
//					Class classToSort;
//					Field keyField = null;
//					
//					try {
//						classToSort = Class.forName(tableToSort.getTableName());
//						keyField = classToSort.getField(attrToIndex);
//					} catch (ClassNotFoundException e2) {
//						e2.printStackTrace();
//					} catch (SecurityException e) {
//						e.printStackTrace();
//					} catch (NoSuchFieldException e) {
//						e.printStackTrace();
//					}
//					
//					Util.sort(tuplesToSort, keyField);
//					
//					// Rename variables
//					Table tableToIndex = tableToSort;
//					ArrayList<Object> tuplesToIndex = tuplesToSort;
//					
//					// Tuples are sorted. Now create a balanced BST index
//					BST<Pair> indexTree = createBalancedBST(tableToIndex, tuplesToIndex, attrToIndex);
//					
//					// Rename variables
//					Table outer = null;
//					Table inner = null;
//					if (table1.equals(tableToIndex)) {
//						inner = table1;
//						outer = table2;
//					} else {
//						inner = table2;
//						outer = table1;
//					}
//					String attr1Name = where.getAttrName();
//					String attr2Name = where.getAttr2Name();
//					String outerAttrVal = "";
//					String innerAttrVal = "";
//					Class classToCheckOuter = null;
//					Class classToCheckInner = null;
//					Method getOuterMethod = null;
//					Method getInnerMethod = null;
//					
//					try {
//						classToCheckOuter = Class.forName(outer.getTableName());
//						classToCheckInner = Class.forName(inner.getTableName());
//						
//						getOuterMethod = extractGetOuterMethod(attr1Name, attr2Name, classToCheckOuter, getOuterMethod);
//						
//						getInnerMethod = extractGetInnerMethod(attr1Name, attr2Name, classToCheckInner, getInnerMethod);
//						
//						
//						// Joined table tuples
//						ArrayList<Object> joinedTableTuples = new ArrayList<Object>();
//						
//						joinedTables = true;
//						
//						// PERFORM NESTED LOOPS JOIN
//						int count = 0;
//						for (int i = 0; i < outer.getTuples().size(); i++) {
//							
//							Object outerTuple = outer.getTuples().get(i);
//							outerAttrVal = getOuterMethod.invoke(outerTuple).toString();
//							
//							// iterate through index tree
//							if (where.getOperator().equals("=")) {
//								Iterator iterator = indexTree.iterator();
//								while (iterator.hasNext()) {
//									Pair currPair = (Pair)iterator.next();
//									ArrayList<Integer> currPairLocations = currPair.getLocation();
//									Object innerTuple = tuplesToIndex.get(currPairLocations.get(0));
//									innerAttrVal = currPair.getKey();
//									
//									if (outerAttrVal.equals(innerAttrVal)) {
//										
//										for (int j = 0; j < currPairLocations.size(); j++) {
//											innerTuple = tuplesToIndex.get(currPairLocations.get(j));
//											innerAttrVal = getInnerMethod.invoke(innerTuple).toString();
//											ArrayList<Object> singleJoinedTuple = new ArrayList<Object>();
//											singleJoinedTuple.add(outerTuple);
//											singleJoinedTuple.add(innerTuple);
//											joinedTableTuples.add(singleJoinedTuple);
//											count++;
//										}
//										
//										break;
//									}
//								}
//							} else if (where.getOperator().equals("<")) {
//								boolean found = false;
//								Iterator iterator = indexTree.iterator();
//								Pair currPair = null;
//								ArrayList<Integer> currPairLocations = new ArrayList<Integer>();
//								Object innerTuple = null;
//								//count = 0;
//								while (iterator.hasNext()) {
//									currPair = (Pair)iterator.next();
//									currPairLocations = currPair.getLocation();
//									innerTuple = tuplesToIndex.get(currPairLocations.get(0));
//									//count++;
//									innerAttrVal = getInnerMethod.invoke(innerTuple).toString();
//									
//									if (Double.parseDouble(outerAttrVal) < Double.parseDouble(innerAttrVal)) {
//										// found first value in range search, add it in all its locations to the table
//										
//										for (int j = 0; j < currPairLocations.size(); j++) {
//											innerTuple = tuplesToIndex.get(currPairLocations.get(j));
//											innerAttrVal = getInnerMethod.invoke(innerTuple).toString();
//											ArrayList<Object> singleJoinedTuple = new ArrayList<Object>();
//											singleJoinedTuple.add(outerTuple);
//											singleJoinedTuple.add(innerTuple);
//											joinedTableTuples.add(singleJoinedTuple);
//											count++;
//										}
//										
//										found = true;
//										break;
//
//									}
//									
//								}
//								
//								if (found == true) {
//
//									while (indexTree.successor(indexTree.getEntry(currPair)) != null) {
//										
//										BST.Entry<Pair> currEntry = indexTree.successor(indexTree.getEntry(currPair));
//										currPair = (Pair)currEntry.element;
//										currPairLocations = currPair.getLocation();
//										for (int j = 0; j < currPairLocations.size(); j++) {
//											innerTuple = tuplesToIndex.get(currPairLocations.get(j));
//											innerAttrVal = getInnerMethod.invoke(innerTuple).toString();
//											ArrayList<Object> singleJoinedTuple = new ArrayList<Object>();
//											singleJoinedTuple.add(outerTuple);
//											singleJoinedTuple.add(innerTuple);
//											joinedTableTuples.add(singleJoinedTuple);
//											count++;
//										}
//									}
//								}
//								
//							} else {
//								
//								boolean found = false;
//								Iterator iterator = indexTree.iterator();
//								Pair currPair = null;
//								ArrayList<Integer> currPairLocations = new ArrayList<Integer>();
//								Object innerTuple = null;
//								
//								while (iterator.hasNext()) {
//									currPair = (Pair)iterator.next();
//									currPairLocations = currPair.getLocation();
//									innerTuple = tuplesToIndex.get(currPairLocations.get(0));
//									innerAttrVal = getInnerMethod.invoke(innerTuple).toString();
//									
//									if (Double.parseDouble(outerAttrVal) <= Double.parseDouble(innerAttrVal)) {
//										
//										// found first value that doesn't qualify in range search.
//										// now find all predecessors
//										found = true;
//										break;
//									}
//									
//								}
//								
//								if (found == true) {
//									
//									while (indexTree.predecessor(indexTree.getEntry(currPair)) != null) {
//										
//										BST.Entry<Pair> currEntry = indexTree.predecessor(indexTree.getEntry(currPair));
//										currPair = (Pair)currEntry.element;
//										currPairLocations = currPair.getLocation();
//										for (int j = 0; j < currPairLocations.size(); j++) {
//											innerTuple = tuplesToIndex.get(currPairLocations.get(j));
//											innerAttrVal = getInnerMethod.invoke(innerTuple).toString();
//											ArrayList<Object> singleJoinedTuple = new ArrayList<Object>();
//											singleJoinedTuple.add(outerTuple);
//											singleJoinedTuple.add(innerTuple);
//											joinedTableTuples.add(singleJoinedTuple);
//											count++;
//										}
//									}
//								}
//							}
//						}
//						printStream.println(count + " tuples selected");
//						
//						// Joined table name
//						String joinedTableName = outer.getTableName() + "-JOIN-" + inner.getTableName();
//						
//						// Joined table attribute names
//						ArrayList<String> joinedAttrNames = new ArrayList<String>();
//						joinedAttrNames.addAll(outer.getAttrNames());
//						joinedAttrNames.addAll(inner.getAttrNames());
//						
//						// Joined table attribute types
//						ArrayList<String> joinedAttrTypes = new ArrayList<String>();
//						joinedAttrTypes.addAll(outer.getAttrTypes());
//						joinedAttrTypes.addAll(inner.getAttrTypes());
//						
//						filteredTable = new Table(joinedTableName, joinedAttrNames, joinedAttrTypes, joinedTableTuples);
//					
//					}
//					catch (ClassNotFoundException e) {
//						System.out.println("ClassNotFoundException2");
//						System.out.println(e.getMessage());
//					}
//					catch (IllegalAccessException e) {
//						System.out.println("ccc");
//						System.out.println(e.getMessage());
//					} catch (SecurityException e) {
//						e.printStackTrace();
//					} catch (Exception e) {
//						// Some other exception
//						e.printStackTrace();
//					}	
//					
					
					
					/*************************************************************************
					 * END OF INDEX JOIN
					 ***********************************************************************/
				}
			}
			
			// Process projection part of query
			ArrayList<String> selectList  = stmt.getSelect();
			
			if (joinedTables == false) { // if each tuple is only made up of one object
				for (int i = 0; i < filteredTable.getTuples().size(); i++) { // for each tuple
					try {
						Class classToCheck = Class.forName(filteredTable.getTableName());
						Object tupleToProject = filteredTable.getTuples().get(i);
						String getMethodName = "";
						Method getMethod = null;
							
						// if select statement is *, project all columns
						if (selectList.get(0).equals("*")) {
							printStream.print("( ");
							String eltToPrint = "";
							
							// for each column except for the last
							for (int j = 0; j < filteredTable.getAttrNames().size() - 1; j++) {
								getMethodName = "get" + filteredTable.getAttrNames().get(j);
								getMethod = classToCheck.getMethod(getMethodName);
								try {
									eltToPrint = getMethod.invoke(tupleToProject).toString();
									printStream.print(eltToPrint + ", ");
								} catch (InvocationTargetException e) {
									System.out.println("22");
									System.out.println(e.getMessage());
								}
							}
							
							// print the last column
							getMethodName = "get" + filteredTable.getAttrNames().get(filteredTable.getAttrNames().size() - 1);
							getMethod = classToCheck.getMethod(getMethodName);
							try {
								eltToPrint = getMethod.invoke(tupleToProject).toString();
								printStream.println(eltToPrint + " )");
							} catch (InvocationTargetException e) {
								System.out.println("22");
								System.out.println(e.getMessage());
							}
						} else {
							
							// find names of column names that we want to project
							ArrayList<String> selectAttrNames = new ArrayList<String>();
							for (int j = 0; j < filteredTable.getAttrNames().size(); j++) {
								for (int k = 0; k < selectList.size(); k++) {
									if (filteredTable.getAttrNames().get(j).equals(selectList.get(k))) {
										selectAttrNames.add(selectList.get(k));
									}
								}
							}

							// print the columns of the tuple that correspond w/selected indices
							int getMethodIndex;
							String eltToPrint = "";
							printStream.print("( ");
							try {
								for (int j = 0; j < selectAttrNames.size() - 1; j++) {
									getMethodName = "get" + selectAttrNames.get(j);
									getMethod = classToCheck.getMethod(getMethodName);
									eltToPrint = getMethod.invoke(tupleToProject).toString();
									printStream.print(eltToPrint + ", ");
								}
								getMethodName = "get" + selectAttrNames.get(selectAttrNames.size() - 1);
								getMethod = classToCheck.getMethod(getMethodName);
								eltToPrint = getMethod.invoke(tupleToProject).toString();
								printStream.println(eltToPrint + ")");
							} catch (InvocationTargetException e) {
								System.out.println("22");
								System.out.println(e.getMessage());
							}
						}
					}
					catch (ClassNotFoundException e) {
						System.out.println("heyError");
						System.out.println(e.getMessage());
					}
					catch (IllegalAccessException e) {
						System.out.println("ccc");
						System.out.println(e.getMessage());
					} catch (SecurityException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (NoSuchMethodException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			} else { // if there are two objects in each tuple (i.e. the table to be projected is a joined table)
				for (int i = 0; i < filteredTable.getTuples().size(); i++) { // for each tuple
					try {
						ArrayList<Object> tupleToProject = (ArrayList<Object>)filteredTable.getTuples().get(i);
						Object firstTable = tupleToProject.get(0);
						Object secondTable = tupleToProject.get(1);
						Class classToCheck1 = Class.forName(table1.getTableName());
						Class classToCheck2 = Class.forName(table2.getTableName());
						String getMethodName = "";
						Method getMethod = null;
							
						// if select statement is *, project all columns
						if (selectList.get(0).equals("*")) {
							printStream.print("( ");
							String eltToPrint = "";
							
							// for each column in the first table
							for (int j = 0; j < table1.getAttrNames().size(); j++) {
								getMethodName = "get" + table1.getAttrNames().get(j);
								getMethod = classToCheck1.getMethod(getMethodName);
								try {
									eltToPrint = getMethod.invoke(firstTable).toString();
									printStream.print(eltToPrint + ", ");
								}  catch (InvocationTargetException e) {
									System.out.println("22");
									System.out.println(e.getMessage());
								}
							}
							
							// for each column in the second table except for the last
							for (int j = 0; j < table2.getAttrNames().size() - 1; j++) {
								getMethodName = "get" + table2.getAttrNames().get(j);
								getMethod = classToCheck2.getMethod(getMethodName);
								try {
									eltToPrint = getMethod.invoke(secondTable).toString();
									printStream.print(eltToPrint + ", ");
								} catch (InvocationTargetException e) {
									System.out.println("22");
									System.out.println(e.getMessage());
								}
							}
							
							// print the last column
							getMethodName = "get" + table2.getAttrNames().get(table2.getAttrNames().size() - 1);
							getMethod = classToCheck2.getMethod(getMethodName);
							try {
								eltToPrint = getMethod.invoke(secondTable).toString();
								printStream.println(eltToPrint + " )");
							} catch (InvocationTargetException e) {
								System.out.println("22");
								System.out.println(e.getMessage());
							}
						} else { // if select statement is a list of attribute names
							ArrayList<String> selectAttrNames1 = new ArrayList<String>();
							ArrayList<String> selectAttrNames2 = new ArrayList<String>();
							
							// for first table object in tuple, create select list
							for (int j = 0; j < table1.getAttrNames().size(); j++) {
								for (int k = 0; k < selectList.size(); k++) {
									if (table1.getAttrNames().get(j).equals(selectList.get(k))) {
										selectAttrNames1.add(selectList.get(k));
									}
								}
							}
							
							// for second table object in tuple, create select list
							for (int j = 0; j < table2.getAttrNames().size(); j++) {
								for (int k = 0; k < selectList.size(); k++) {
									if (table2.getAttrNames().get(j).equals(selectList.get(k))) {
										selectAttrNames2.add(selectList.get(k));
									}
								}
							}

							// print the appropriate columns of the tuple
							String eltToPrint = "";
							printStream.print("( ");
							try {
								if (selectAttrNames1.size() >= 2) {
									
									// for all elements in first table except for last
									for (int j = 0; j < selectAttrNames1.size() - 1; j++) {
										getMethodName = "get" + selectAttrNames1.get(j);
										getMethod = classToCheck1.getMethod(getMethodName);
										eltToPrint = getMethod.invoke(firstTable).toString();
										printStream.print(eltToPrint + ", ");
									}
									
									// no elements in second table, print last element in first table and close parentheses
									if (selectAttrNames2.size() == 0) {
										getMethodName = "get" + selectAttrNames1.get(selectAttrNames1.size() - 1);
										getMethod = classToCheck1.getMethod(getMethodName);
										eltToPrint = getMethod.invoke(firstTable).toString();
										printStream.println(eltToPrint + " )");	
									} else if (selectAttrNames2.size() == 1) { // print last element in first table, then element in second table, then close parentheses
										getMethodName = "get" + selectAttrNames1.get(selectAttrNames1.size() - 1);
										getMethod = classToCheck1.getMethod(getMethodName);
										eltToPrint = getMethod.invoke(firstTable).toString();
										printStream.print(eltToPrint + ", ");
										
										getMethodName = "get" + selectAttrNames2.get(0);
										getMethod = classToCheck2.getMethod(getMethodName);
										eltToPrint = getMethod.invoke(secondTable).toString();
										printStream.println(eltToPrint + " )");
			
									} else { // print last element in first table, then every element but last in second table, then print last element in second table and close parenthese
										getMethodName = "get" + selectAttrNames1.get(selectAttrNames1.size() - 1);
										getMethod = classToCheck1.getMethod(getMethodName);
										eltToPrint = getMethod.invoke(firstTable).toString();
										printStream.print(eltToPrint + ", ");
										
										// for all elements in second table except for last
										for (int j = 0; j < selectAttrNames2.size() - 1; j++) {
											getMethodName = "get" + selectAttrNames2.get(j);
											getMethod = classToCheck2.getMethod(getMethodName);
											eltToPrint = getMethod.invoke(secondTable).toString();
											printStream.print(eltToPrint + ", ");
										}
										
										getMethodName = "get" + selectAttrNames2.get(0);
										getMethod = classToCheck2.getMethod(getMethodName);
										eltToPrint = getMethod.invoke(secondTable).toString();
										printStream.println(eltToPrint + " )");
									}
								} else if (selectAttrNames1.size() == 1) {
									// no elements in second table, print last element in first table and close parentheses
									if (selectAttrNames2.size() == 0) {
										getMethodName = "get" + selectAttrNames1.get(selectAttrNames1.size() - 1);
										getMethod = classToCheck1.getMethod(getMethodName);
										eltToPrint = getMethod.invoke(firstTable).toString();
										printStream.println(eltToPrint + " )");	
									} else if (selectAttrNames2.size() == 1) { // print last element in first table, then element in second table, then close parentheses
										getMethodName = "get" + selectAttrNames1.get(selectAttrNames1.size() - 1);
										getMethod = classToCheck1.getMethod(getMethodName);
										eltToPrint = getMethod.invoke(firstTable).toString();
										printStream.print(eltToPrint + ", ");
										
										getMethodName = "get" + selectAttrNames2.get(0);
										getMethod = classToCheck2.getMethod(getMethodName);
										eltToPrint = getMethod.invoke(secondTable).toString();
										printStream.println(eltToPrint + " )");
			
									} else { // print last element in first table, then every element but last in second table, then print last element in second table and close parenthese
										getMethodName = "get" + selectAttrNames1.get(selectAttrNames1.size() - 1);
										getMethod = classToCheck1.getMethod(getMethodName);
										eltToPrint = getMethod.invoke(firstTable).toString();
										printStream.print(eltToPrint + ", ");
										
										// for all elements in second table except for last
										for (int j = 0; j < selectAttrNames2.size() - 1; j++) {
											getMethodName = "get" + selectAttrNames2.get(j);
											getMethod = classToCheck2.getMethod(getMethodName);
											eltToPrint = getMethod.invoke(secondTable).toString();
											printStream.print(eltToPrint + ", ");
										}
										
										getMethodName = "get" + selectAttrNames2.get(0);
										getMethod = classToCheck2.getMethod(getMethodName);
										eltToPrint = getMethod.invoke(secondTable).toString();
										printStream.println(eltToPrint + " )");
									}
								} else { // if no elements in selectAttrNames1
									if (selectAttrNames2.size() == 1) { // print single element in second table, then close parentheses
										getMethodName = "get" + selectAttrNames2.get(0);
										getMethod = classToCheck2.getMethod(getMethodName);
										eltToPrint = getMethod.invoke(secondTable).toString();
										printStream.println(eltToPrint + " )");
			
									} else { // print every element but last in second table, then print last element in second table and close parentheses
										
										// for all elements in second table except for last
										for (int j = 0; j < selectAttrNames2.size() - 1; j++) {
											getMethodName = "get" + selectAttrNames2.get(j);
											getMethod = classToCheck2.getMethod(getMethodName);
											eltToPrint = getMethod.invoke(secondTable).toString();
											printStream.print(eltToPrint + ", ");
										}
										
										getMethodName = "get" + selectAttrNames2.get(0);
										getMethod = classToCheck2.getMethod(getMethodName);
										eltToPrint = getMethod.invoke(secondTable).toString();
										printStream.println(eltToPrint + " )");
									}
								}
							} catch (InvocationTargetException e) {
								System.out.println("22");
								System.out.println(e.getMessage());
							}
						}
					}
					catch (ClassNotFoundException e) {
						System.out.println("ClassNotFoundException");
						System.out.println(e.getMessage());
					}
					catch (IllegalAccessException e) {
						System.out.println("ccc");
						System.out.println(e.getMessage());
					} catch (SecurityException e1) {
						e1.printStackTrace();
					} catch (NoSuchMethodException e1) {
						e1.printStackTrace();
					}
				}
			}
		} else {
			printStream.println("ERROR: No database currently in use");
		}
	}

	private static void sortTable(Table table1, String sortByAttr1) {
		ArrayList<Object> table1SortedTuples = table1.getTuples();
		Class table1Class;
		Field keyField1 = null;
		try {
			table1Class = Class.forName(table1.getTableName());
			keyField1 = table1Class.getField(sortByAttr1);
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
		
		Util.sort(table1SortedTuples, keyField1);
	}

	private static Method extractGetInnerMethod(String attr1Name,
			String attr2Name, Class classToCheckInner, Method getInnerMethod) {
		String getInnerMethodName;
		try {
			getInnerMethodName = "get" + attr2Name;
			getInnerMethod = classToCheckInner.getMethod(getInnerMethodName);
		} catch (NoSuchMethodException e) {
			//System.out.println("NoSuchMethodException1");
			//e.printStackTrace();
			try {
				getInnerMethodName = "get" + attr1Name;
				getInnerMethod = classToCheckInner.getMethod(getInnerMethodName);
			} catch (NoSuchMethodException e2) {
				System.out.println("NoSuchMethodException2 for Inner Method");
				e.printStackTrace();
			}
		}
		return getInnerMethod;
	}

	private static Method extractGetOuterMethod(String attr1Name,
			String attr2Name, Class classToCheckOuter, Method getOuterMethod) {
		String getOuterMethodName;
		try {
			getOuterMethodName = "get" + attr1Name;
			getOuterMethod = classToCheckOuter.getMethod(getOuterMethodName);
		} catch (NoSuchMethodException e) {
			//System.out.println("NoSuchMethodException1");
			//e.printStackTrace();
			try {
				getOuterMethodName = "get" + attr2Name;
				getOuterMethod = classToCheckOuter.getMethod(getOuterMethodName);
			} catch (NoSuchMethodException e2) {
				System.out.println("NoSuchMethodException2 for Outer Method");
				e.printStackTrace();
			}
		}
		return getOuterMethod;
	}

	private static BST<Pair> createBalancedBST(Table tableToIndex, ArrayList<Object> tuplesToIndex, String attrToIndex) {
		BST<Pair> balancedBST = new BST<Pair>();
		
		sortTableToBST(balancedBST, tableToIndex, tuplesToIndex, attrToIndex, 0, tuplesToIndex.size() - 1);
		return balancedBST;
	}
	
	private static void sortTableToBST(BST<Pair> indexTree, Table tableToIndex, ArrayList<Object> tuplesToIndex, String attrToIndex, int start, int end) {
		if (start > end) {
			return;
		}
		int mid = start + (end - start) / 2;
		
		Pair eltToAddToIndex = makePairForObject(attrToIndex, tableToIndex, tuplesToIndex, mid);
		
		indexTree.add(eltToAddToIndex);
		
		sortTableToBST(indexTree, tableToIndex, tuplesToIndex, attrToIndex, start, mid - 1);
		sortTableToBST(indexTree, tableToIndex, tuplesToIndex, attrToIndex, mid + 1, end);
	}

	private static Pair makePairForObject(String attrToIndex, Table tableToIndex, ArrayList<Object> tuplesToIndex, int mid) {
		
		Object midObject = tuplesToIndex.get(mid);
		String key = "";
		ArrayList<Integer> location = new ArrayList<Integer>();
		
		try {
			Class classToCheck = Class.forName(tableToIndex.getTableName());
			String getMethodName = "get" + attrToIndex;
			Method getMethod = classToCheck.getMethod(getMethodName);
			
			key = getMethod.invoke(midObject).toString();
		} 
		catch (ClassNotFoundException e) {
			System.out.println("ClassNotFoundException2");
			System.out.println(e.getMessage());
		}
		catch (IllegalAccessException e) {
			System.out.println("ccc");
			System.out.println(e.getMessage());
		} 
		catch (SecurityException e) {
			e.printStackTrace();
		} 
		catch (NoSuchMethodException e) {
			System.out.println("NoSuchMethodException2");
			e.printStackTrace();
		}
		catch (Exception e) {
			// Some other exception
			e.printStackTrace();
		}	
		
		location.add(mid);
		
		Pair pairForObject = new Pair(key, location);
		
		return pairForObject;
	}

	private static Table filterBySelectionConditions(Table table, EqualExpression where, Boolean joinedTables, Table table1, Table table2) {
		Table filteredTable = null;
		ArrayList<Object> filteredTableTuples = new ArrayList<Object>();
		String whereAttrName = where.getAttrName();
		String whereAttrValue = where.getAttrVal();
		String whereAttr2Name = where.getAttr2Name();
		String tupleAttrValue = "";
		
		// Find corresponding attribute value(s) in each tuple and check for equivalence. If equal, select the tuple
		
		if (joinedTables == false) {
			// for each tuple
			for (int i = 0; i < table.getTuples().size(); i++) {
				try {
					Class classToCheck = Class.forName(table.getTableName());
					Object tupleToSelect = table.getTuples().get(i);
					String getMethodName = "get" + whereAttrName;
					Method getMethod = classToCheck.getMethod(getMethodName);
	
					try {
						tupleAttrValue = getMethod.invoke(tupleToSelect).toString();
					} catch (InvocationTargetException e) {
						System.out.println("22");
						System.out.println(e.getMessage());
					}
					
					if (where.getOperator() == "=") {
						if (whereAttrValue.equals(tupleAttrValue)) {
							filteredTableTuples.add(tupleToSelect);
						}
					} else if (where.getOperator() == "<") {
						if (Double.parseDouble(tupleAttrValue) < Double.parseDouble(whereAttrValue)) {
							filteredTableTuples.add(tupleToSelect);
						}
					} else {
						if (Double.parseDouble(tupleAttrValue) > Double.parseDouble(whereAttrValue)) {
							filteredTableTuples.add(tupleToSelect);
						}
					}
				}
				catch (ClassNotFoundException e) {
					System.out.println("ClassNotFoundException");
					System.out.println(e.getMessage());
				}
				catch (IllegalAccessException e) {
					System.out.println("ccc");
					System.out.println(e.getMessage());
				} 
				catch (SecurityException e1) {
					e1.printStackTrace();
				} 
				catch (NoSuchMethodException e1) {
					e1.printStackTrace();
				}
			}
		} else { // if we need to process a joined table (NOTE: attr op attr expressions only occur in the case of a joined table
			
			// for each tuple
			for (int i = 0; i < table.getTuples().size(); i++) {
				try { // check for table1
					ArrayList<Object> tupleToSelect = (ArrayList<Object>)table.getTuples().get(i);
					
					Class classToCheck = Class.forName(table1.getTableName());
					String getMethodName = "get" + whereAttrName;
					Method getMethod = classToCheck.getMethod(getMethodName);
	
					try {
						tupleAttrValue = getMethod.invoke(tupleToSelect.get(0)).toString();
					} catch (InvocationTargetException e) {
						System.out.println("22");
						System.out.println(e.getMessage());
					}
					
					if (where.getOperator() == "=") {
						if (whereAttrValue.equals(tupleAttrValue)) {
							filteredTableTuples.add(tupleToSelect);
						}
					} else if (where.getOperator() == "<") {
						if (Double.parseDouble(tupleAttrValue) < Double.parseDouble(whereAttrValue)) {
							filteredTableTuples.add(tupleToSelect);
						}
					} else {
						if (Double.parseDouble(tupleAttrValue) > Double.parseDouble(whereAttrValue)) {
							filteredTableTuples.add(tupleToSelect);
						}
					}
				}
				catch (ClassNotFoundException e) {
					System.out.println("ClassNotFoundException1");
					System.out.println(e.getMessage());
				}
				catch (IllegalAccessException e) {
					System.out.println("ccc");
					System.out.println(e.getMessage());
				} catch (SecurityException e1) {
					e1.printStackTrace();
				} catch (NoSuchMethodException e1) { // check for table2
					//System.out.println("NoSuchMethodException1");
					//e1.printStackTrace();
					try {
						Class classToCheck = Class.forName(table2.getTableName());
						ArrayList<Object> tupleToSelect = (ArrayList<Object>)table.getTuples().get(i);
						String getMethodName = "get" + whereAttrName;
						Method getMethod = classToCheck.getMethod(getMethodName);
		
						try {
							tupleAttrValue = getMethod.invoke(tupleToSelect.get(1)).toString();
						} catch (InvocationTargetException e) {
							System.out.println("22");
							System.out.println(e.getMessage());
						}
						
						if (where.getOperator() == "=") {
							if (whereAttrValue.equals(tupleAttrValue)) {
								filteredTableTuples.add(tupleToSelect);
							}
						} else if (where.getOperator() == "<") {
							if (Double.parseDouble(tupleAttrValue) < Double.parseDouble(whereAttrValue)) {
								filteredTableTuples.add(tupleToSelect);
							}
						} else {
							if (Double.parseDouble(tupleAttrValue) > Double.parseDouble(whereAttrValue)) {
								filteredTableTuples.add(tupleToSelect);
							}
						}
					}
					catch (ClassNotFoundException e) {
						System.out.println("ClassNotFoundException2");
						System.out.println(e.getMessage());
					}
					catch (IllegalAccessException e) {
						System.out.println("ccc");
						System.out.println(e.getMessage());
					} catch (SecurityException e) {
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						System.out.println("NoSuchMethodException2");
						e.printStackTrace();
					} catch (Exception e) {
						// Some other exception
						e.printStackTrace();
					}	
				} catch (Exception e) {
					// Some other exception
					e.printStackTrace();
				}
			}
		}
		
		filteredTable = new Table(table.getTableName(), table.getAttrNames(), table.getAttrTypes(), filteredTableTuples);
		
		return filteredTable;
	}

	private static Table joinTables(Table table1, Table table2) {
		// Joined table name
		String joinedTableName = table1.getTableName() + "-CROSS-" + table2.getTableName();
		
		// Joined table attribute names
		ArrayList<String> joinedAttrNames = new ArrayList<String>();
		joinedAttrNames.addAll(table1.getAttrNames());
		joinedAttrNames.addAll(table2.getAttrNames());
		
		// Joined table attribute types
		ArrayList<String> joinedAttrTypes = new ArrayList<String>();
		joinedAttrTypes.addAll(table1.getAttrTypes());
		joinedAttrTypes.addAll(table2.getAttrTypes());
		
		// Joined table tuples
		ArrayList<Object> joinedTableTuples = new ArrayList<Object>();
		for (int i = 0; i < table1.getTuples().size(); i++) { // loop through all tuples in table 1
			Object tuple1 = table1.getTuples().get(i);
			for (int j = 0; j < table2.getTuples().size(); j++) { // loop through all tuples in table 2
				Object tuple2 = table2.getTuples().get(j);
				
				ArrayList<Object> singleJoinedTuple = new ArrayList<Object>();
				singleJoinedTuple.add(tuple1);
				singleJoinedTuple.add(tuple2);
				joinedTableTuples.add(singleJoinedTuple);
			}
		}
		
		Table joinedTable = new Table(joinedTableName, joinedAttrNames, joinedAttrTypes, joinedTableTuples);
		return joinedTable;
	}
	
	private static Table getTable(DBMS currentDB, String tableName, PrintStream printStream) {
		Table table = null;
		int index = -1;
		
		// find index of table that we want to select
		for (int i = 0; i < currentDB.getDbTables().size(); i++) {
			if (currentDB.getDbTables().get(i).getTableName().equals(tableName)) {
				index = i;
				break;
			}
		}
		if (index != -1) {
			table = currentDB.getDbTables().get(index);
		} else {
			printStream.println("ERROR: Table does not exist");
		}
		return table;
		
	}

}
