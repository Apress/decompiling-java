/*
 *  Copyright (C) 2004 Godfrey Nolan  
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.*;
import java.io.*;
import java.util.*;


public class XMLToClass
{

	static Document doc;
	static DataOutputStream out;


	static Document xmlDoc;
	static String[][] cnstPool;
	static NodeList opCodeNodes;
	static int methodVarNbr;

	static boolean DebugMode = false;

	public static void main (String[] args)
	throws IOException, DOMException
	{

		String fileInName = "";
		String fileOutName = "";
		int i = 0;

		//validate input parameters
		if ((args.length < 1) || (args[0].endsWith(".xml") == false) || (args[1].endsWith(".class") == false) ) {
			System.out.println("\n\nUsage = 'java XMLToClass <input_xml_file.xml> <output_class_file.class>'\n");
			System.exit (0);
		} else if (args.length==3 && args[2].equals("-d")) {
			DebugMode=true;
			fileInName = args[0];
			fileOutName = args[1];
		} else {
			fileInName = args[0];
			fileOutName = args[1];
		}


		try {
			//open input xml file and create DOM
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
        	doc = builder.parse( new File(fileInName) );

        	//open output stream for class file
        	out = new DataOutputStream(new FileOutputStream(fileOutName));

        	//CAFEBABE
        	out.writeByte(hexToDecimal("CA"));
        	out.writeByte(hexToDecimal("FE"));
        	out.writeByte(hexToDecimal("BA"));
        	out.writeByte(hexToDecimal("BE"));

        	//minor version
          	out.writeShort(Short.valueOf(getNodeValue(doc, "MinorVersion").trim()).shortValue());

        	//major version
          	out.writeShort(Short.valueOf(getNodeValue(doc, "MajorVersion").trim()).shortValue());

        	//constant pool count
          	out.writeShort(Short.valueOf(getNodeValue(doc, "ConstantPool_Count").trim()).shortValue());

        	//constant pool
			writeConstantPool(doc,out);

        	//access flags
        	Node accessFlagsNode = doc.getElementsByTagName("AccessFlags").item(0).getChildNodes().item(0);
        	String ClassAccess = accessFlagsNode.getNodeValue();
        	int typecode=0x20;

			if (ClassAccess.indexOf("public ")!=-1)
					typecode += 1;
			else if (ClassAccess.indexOf("public ")!=-1)
					typecode += 2;
			else if (ClassAccess.indexOf("protected ")!=-1)
					typecode += 4;

			if (ClassAccess.indexOf("static ")!=-1)
					typecode += 8;
			if (ClassAccess.indexOf("final ")!=-1)
					typecode += 0x10;
			if (ClassAccess.indexOf("volatile ")!=-1)
					typecode += 0x40;
			if (ClassAccess.indexOf("interface ")!=-1)
					typecode += 0x200;
			if (ClassAccess.indexOf("abstract ")!=-1)
					typecode += 0x400;

			//String hexAccessFlag = accessFlagsNode.getNodeValue();
			//String decimalAccessFlag = hexAccessFlag.substring(2,hexAccessFlag.length());
    		//Integer intAccessFlag = new Integer(hexToDecimal(decimalAccessFlag));
         	out.writeShort((short)typecode);

		 	//this class
        	Node thisClassNode = doc.getElementsByTagName("ThisClass").item(0).getChildNodes().item(0);
    		Integer intThisClass = new Integer(thisClassNode.getNodeValue().trim());
         	out.writeShort(intThisClass.shortValue());

		 	//super class
        	Node superClassNode = doc.getElementsByTagName("SuperClass").item(0).getChildNodes().item(0);
    		Integer intSuperClass = new Integer(superClassNode.getNodeValue().trim());
         	out.writeShort(intSuperClass.shortValue());

        	//interfaces count
        	out.writeShort(Integer.valueOf(getNodeValue(doc, "Interface_Count").trim()).intValue());

        	//interfaces
			NodeList interfaces = doc.getElementsByTagName("Interfaces").item(0).getChildNodes().item(0).getChildNodes();
			int nbrInterfaces = interfaces.getLength();
			for (i = 0; i < nbrInterfaces; i++) {
				Node interfaceNode = interfaces.item(i).getChildNodes().item(0);
				out.writeShort(Short.valueOf(interfaceNode.getChildNodes().item(0).getNodeValue().trim()).shortValue());
        	}

        	//fields count
        	out.writeShort(Integer.valueOf(getNodeValue(doc, "Field_Count").trim()).intValue());

        	//fields
        	writeFields(doc,out);

        	//methods count
        	out.writeShort(Integer.valueOf(getNodeValue(doc, "Method_Count").trim()).shortValue());

        	//methods
        	writeMethods(doc,out);

        	//attributes
        	NodeList attList = doc.getElementsByTagName("Attributes");
        	Element attributes = (Element) attList.item(attList.getLength()-1);

        	//attributes count
        	NodeList attCountList = doc.getElementsByTagName("Attribute_Count");
	    	Integer classAttrCount = new Integer(attCountList.item(attList.getLength()-1).getChildNodes().item(0).getNodeValue().trim());
	        out.writeShort(classAttrCount.shortValue());
	        if (DebugMode)
	        	System.out.println("No. of class attributes: " + classAttrCount);
        	//out.writeShort(Integer.valueOf(getElementsByTagName("Attribute_Count").item(0).getChildNodes().getNodeValue()).intValue());
        	//out.writeShort(Integer.valueOf(getNodeValue(doc, "Attribute_Count")).shortValue());
			writeAttributes(doc,out,attributes);

        	out.close();
			System.exit (0);
		} catch (ParserConfigurationException pce) {
        	pce.printStackTrace();
    	} catch (SAXException pce) {
			pce.printStackTrace();
		} catch (IOException e) {
			System.err.println("File cannot be read:" + e.getMessage());
		} catch (DOMException e) {
			System.err.println("DOM exception:" + e.getMessage());
		}

	} //end of main method


	//write the constant pool
	static void writeConstantPool(Document doc, DataOutputStream out) {
		try {
			NodeList constants = doc.getElementsByTagName("ConstantPool").item(0).getChildNodes();
			int nbrConstants = constants.getLength();
			boolean skipDouble=false;
			for (int i = 1; i < nbrConstants; i+=2) {
				Node typeNode = constants.item(i).getChildNodes().item(3);
				String type = typeNode.getChildNodes().item(0).getNodeValue();

				//CONSTANT_Utf8
				if (type.compareTo("CONSTANT_Utf8")==0) {
					out.writeByte(1);
					Node valueNode = constants.item(i).getChildNodes().item(5);
					String value = valueNode.getChildNodes().item(0).getNodeValue().substring(9);
					value = value.substring(0,value.length()-1);
					while (value.indexOf("\\ ")!=-1)
						value = value.substring(0,value.indexOf("\\ ")) + value.substring(value.indexOf("\\ ")+1);
					Integer length = new Integer(value.length());
					out.writeShort(length.shortValue());
					out.writeBytes(value);
				}


				//CONSTANT_Integer
				if (type.compareTo("CONSTANT_Integer")==0) {
					out.writeByte(3);
					Node valueNode = constants.item(i).getChildNodes().item(5);
					String tmp = valueNode.getChildNodes().item(0).getNodeValue();
					Integer value = new Integer(tmp.trim());
					out.writeInt(value.intValue());
				}


				//CONSTANT_Float
				if (type.compareTo("CONSTANT_Float")==0) {
					out.writeByte(4);
					Node valueNode = constants.item(i).getChildNodes().item(5);
					String tmp = valueNode.getChildNodes().item(0).getNodeValue();
					Float value = new Float(tmp.trim());
					out.writeFloat(value.floatValue());
				}


				//CONSTANT_Long
				if (type.compareTo("CONSTANT_Long")==0) {
					if (!skipDouble) {
						out.writeByte(5);
						Node valueNode = constants.item(i).getChildNodes().item(5);
						String tmp = valueNode.getChildNodes().item(0).getNodeValue();
						Long value = new Long(tmp.trim());
						out.writeLong(value.longValue());
						skipDouble=true;
					}
					else {
						skipDouble=false;
					}
				}


				//CONSTANT_Double
				if (type.compareTo("CONSTANT_Double")==0) {
					if (!skipDouble) {
						out.writeByte(6);
						Node valueNode = constants.item(i).getChildNodes().item(5);
						String tmp = valueNode.getChildNodes().item(0).getNodeValue();
						Double value = new Double(tmp.trim());
						out.writeDouble(value.doubleValue());
						skipDouble=true;
					}
					else {
						skipDouble=false;
					}
				}


				//CONSTANT_Class
				if (type.compareTo("CONSTANT_Class")==0) {
					out.writeByte(7);
					Node valueNode = constants.item(i).getChildNodes().item(5);
					String tmp = valueNode.getChildNodes().item(0).getNodeValue();
					Short value = new Short(tmp.trim());
					out.writeShort(value.shortValue());
				}


				//CONSTANT_String
				if (type.compareTo("CONSTANT_String")==0) {
					out.writeByte(8);
					Node valueNode = constants.item(i).getChildNodes().item(5);
					String tmp = valueNode.getChildNodes().item(0).getNodeValue();
					Short value = new Short(tmp.trim());
					out.writeShort(value.shortValue());
				}


				//CONSTANT_Fieldref
				if (type.compareTo("CONSTANT_Fieldref")==0) {
					out.writeByte(9);
					Node valueNode = constants.item(i).getChildNodes().item(5);
					String tmp = valueNode.getChildNodes().item(0).getNodeValue();
					//class index
					Short value = new Short(tmp.substring(0,tmp.indexOf(",")).trim());
					out.writeShort(value.shortValue());
					//name type index
					Short value2 = new Short(tmp.substring(tmp.indexOf(",")+1).trim());
					out.writeShort(value2.shortValue());
				}


				//CONSTANT_Methodref
				if (type.compareTo("CONSTANT_Methodref")==0) {
					out.writeByte(10);
					Node valueNode = constants.item(i).getChildNodes().item(5);
					String tmp = valueNode.getChildNodes().item(0).getNodeValue();
					//class index
					Short value = new Short(tmp.substring(0,tmp.indexOf(",")).trim());
					out.writeShort(value.shortValue());
					//name type index
					Short value2 = new Short(tmp.substring(tmp.indexOf(",")+1).trim());
					out.writeShort(value2.shortValue());
				}


				//CONSTANT_InterfaceMethodRef
				if (type.compareTo("CONSTANT_InterfaceMethodRef")==0) {
					out.writeByte(11);
					Node valueNode = constants.item(i).getChildNodes().item(5);
					String tmp = valueNode.getChildNodes().item(0).getNodeValue();
					//class index
					Short value = new Short(tmp.substring(0,tmp.indexOf(",")).trim());
					out.writeShort(value.shortValue());
					//name type index
					Short value2 = new Short(tmp.substring(tmp.indexOf(",")+1).trim());
					out.writeShort(value2.shortValue());
				}


				//CONSTANT_NameAndType
				if (type.compareTo("CONSTANT_NameAndType")==0) {
					out.writeByte(12);
					Node valueNode = constants.item(i).getChildNodes().item(5);
					String tmp = valueNode.getChildNodes().item(0).getNodeValue();
					//class index
					Short value = new Short(tmp.substring(0,tmp.indexOf(",")).trim());
					out.writeShort(value.shortValue());
					//name type index
					Short value2 = new Short(tmp.substring(tmp.indexOf(",")+1).trim());
					out.writeShort(value2.shortValue());
				}
			}
		} catch (IOException e) {
			System.err.println("File cannot be read:" + e.getMessage());
		} catch (DOMException e) {
			System.err.println("DOM exception:" + e.getMessage());
		}

	}

	//write the fields
	static void writeFields(Document doc, DataOutputStream out) {
		try {
			NodeList fields = doc.getElementsByTagName("Fields").item(0).getChildNodes();
			int nbrFields = fields.getLength();
			for (int i = 1; i < nbrFields; i+=2) {
				Element field = (Element)(fields.item(i));

				//access flags
    	    	String ClassAccess = field.getElementsByTagName("AccessFlags").item(0).getChildNodes().item(0).getNodeValue();
    	    	int typecode=0x00;

				if (ClassAccess.indexOf("public")!=-1)
						typecode += 1;
				else if (ClassAccess.indexOf("private")!=-1)
						typecode += 2;
				else if (ClassAccess.indexOf("protected")!=-1)
						typecode += 4;

				if (ClassAccess.indexOf("static")!=-1)
						typecode += 8;
				if (ClassAccess.indexOf("final")!=-1)
						typecode += 0x10;
				if (ClassAccess.indexOf("volatile")!=-1)
						typecode += 0x40;
				if (ClassAccess.indexOf("interface")!=-1)
						typecode += 0x200;
				if (ClassAccess.indexOf("abstract")!=-1)
						typecode += 0x400;
         		out.writeShort((short)typecode);

	         	//name index
	    		Integer nameIndex = new Integer(field.getElementsByTagName("Name_Index").item(0).getChildNodes().item(0).getNodeValue().trim());
	         	out.writeShort(nameIndex.shortValue());

	         	//descriptor index
	    		Integer descriptorIndex = new Integer(field.getElementsByTagName("Description_Index").item(0).getChildNodes().item(0).getNodeValue().trim());
	         	out.writeShort(descriptorIndex.shortValue());

	         	//attribute count
	    		Integer attributeCount = new Integer(field.getElementsByTagName("Attribute_Count").item(0).getChildNodes().item(0).getNodeValue().trim());
	         	out.writeShort(attributeCount.shortValue());

	         	//attributes
				writeAttributes(doc,out,field);

			}
		} catch (IOException e) {
			System.err.println("File cannot be read:" + e.getMessage());
		} catch (DOMException e) {
			System.err.println("DOM exception:" + e.getMessage());
		}

	}

	//write the methods
	static void writeMethods(Document doc, DataOutputStream out) {
		try {
			NodeList methods = doc.getElementsByTagName("Methods").item(0).getChildNodes();
			int nbrMethods = methods.getLength();
			for (int i = 1; i < nbrMethods; i+=2)
			 {
				Element method = (Element) methods.item(i);

				//access flags
				String ClassAccess = method.getElementsByTagName("AccessFlags").item(0).getChildNodes().item(0).getNodeValue();
    	    	int typecode=0x0;

				if (ClassAccess.indexOf("public ")!=-1)
						typecode += 1;
				else if (ClassAccess.indexOf("public ")!=-1)
						typecode += 2;
				else if (ClassAccess.indexOf("protected ")!=-1)
						typecode += 4;

				if (ClassAccess.indexOf("static ")!=-1)
						typecode += 8;
				if (ClassAccess.indexOf("final ")!=-1)
						typecode += 0x10;
				if (ClassAccess.indexOf("volatile ")!=-1)
						typecode += 0x40;
				if (ClassAccess.indexOf("interface ")!=-1)
						typecode += 0x200;
				if (ClassAccess.indexOf("abstract ")!=-1)
						typecode += 0x400;
         		out.writeShort((short)typecode);

	         	//name index
	    		Integer nameIndex = new Integer(method.getElementsByTagName("Name_Index").item(0).getChildNodes().item(0).getNodeValue().trim());
	         	out.writeShort(nameIndex.shortValue());

	         	//descriptor index
	    		Integer descriptorIndex = new Integer(method.getElementsByTagName("Description_Index").item(0).getChildNodes().item(0).getNodeValue().trim());
	         	out.writeShort(descriptorIndex.shortValue());

	         	//attribute count
	    		Integer attributeCount = new Integer(method.getElementsByTagName("Attribute_Count").item(0).getChildNodes().item(0).getNodeValue().trim());
	         	out.writeShort(attributeCount.shortValue());

	         	//attributes
				writeAttributes(doc,out,method);

			}
		} catch (IOException e) {
			System.err.println("File cannot be read:" + e.getMessage());
		} catch (DOMException e) {
			System.err.println("DOM exception:" + e.getMessage());
		}

	}


	static void writeAttributes(Document doc, DataOutputStream out, Element attrRoot) {
		try {
			NodeList attributes;
			if (!attrRoot.getNodeName().equals("Attributes"))
				attributes = attrRoot.getElementsByTagName("Attributes").item(0).getChildNodes();
			else
			  {
				attributes = attrRoot.getChildNodes();
		      }

			int nbrAttributes = attributes.getLength();
			for (int i = 1; i < nbrAttributes; i+=2) {
				Element attrInfo = (Element) attributes.item(i);
				String attrType = attrInfo.getElementsByTagName("Attribute_Type").item(0).getChildNodes().item(0).getNodeValue();
				int attrTypeIndex = Integer.valueOf(attrInfo.getElementsByTagName("Attribute_Type_Index").item(0).getChildNodes().item(0).getNodeValue().trim()).intValue();

				//attribute name index
				out.writeShort(attrTypeIndex);

				//attribute length
				int attrLength = Integer.valueOf(attrInfo.getElementsByTagName("Attribute_Length").item(0).getChildNodes().item(0).getNodeValue().trim()).intValue();
				out.writeInt(attrLength);

				//code
				if (attrType.equals("Code")) {
					//max stack
					int maxStack = Integer.valueOf(attrInfo.getElementsByTagName("Max_Stack").item(0).getChildNodes().item(0).getNodeValue().trim()).intValue();
					out.writeShort(maxStack);

					//min stack
					int minStack = Integer.valueOf(attrInfo.getElementsByTagName("Num_Locals").item(0).getChildNodes().item(0).getNodeValue().trim()).intValue();
					out.writeShort(minStack);

					//code length
					int codeLength = Integer.valueOf(attrInfo.getElementsByTagName("Code_Length").item(0).getChildNodes().item(0).getNodeValue().trim()).intValue();
					out.writeInt(codeLength);

					//code
					NodeList codelines = attrInfo.getElementsByTagName("Code").item(0).getChildNodes();
					int nbrCodeLines = codelines.getLength()-1;
					String codeBytes = "";
					int tempNum = 0;

					for (int j = 1; j < nbrCodeLines; j+=2)
					  {
						Node codeLine = codelines.item(j);
						String tempLine = codeLine.getChildNodes().item(0).getNodeValue().trim();
						int lineNum = Integer.parseInt(tempLine.substring(0,tempLine.indexOf(":")).trim());
						if (DebugMode)
							System.out.println("Line number: " + lineNum);
						tempLine = tempLine.substring(tempLine.indexOf(":")+1).trim();

						if (tempLine.startsWith("nop"))
							codeBytes += "00";
						else if (tempLine.startsWith("aconst_null"))
							codeBytes += "01";
						else if (tempLine.startsWith("iconst_m1"))
							codeBytes += "02";
						else if (tempLine.startsWith("iconst_0"))
							codeBytes += "03";
						else if (tempLine.startsWith("iconst_1"))
							codeBytes += "04";
						else if (tempLine.startsWith("iconst_2"))
							codeBytes += "05";
						else if (tempLine.startsWith("iconst_3"))
							codeBytes += "06";
						else if (tempLine.startsWith("iconst_4"))
							codeBytes += "07";
						else if (tempLine.startsWith("iconst_5"))
							codeBytes += "08";
						else if (tempLine.startsWith("lconst_0"))
							codeBytes += "09";
						else if (tempLine.startsWith("lconst_1"))
							codeBytes += "0A";
						else if (tempLine.startsWith("fconst_0"))
							codeBytes += "0B";
						else if (tempLine.startsWith("fconst_1"))
							codeBytes += "0C";
						else if (tempLine.startsWith("fconst_2"))
							codeBytes += "0D";
						else if (tempLine.startsWith("dconst_0"))
							codeBytes += "0E";
						else if (tempLine.startsWith("dconst_1"))
							codeBytes += "0F";
						else if (tempLine.startsWith("bipush"))
						  {
							codeBytes += "10";
							String temp = tempLine.substring(tempLine.indexOf("bipush")+7).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFF) + 0x100, 16).substring(1);
					      }
						else if (tempLine.startsWith("sipush"))
						  {
							codeBytes += "11";
							String temp = tempLine.substring(tempLine.indexOf("sipush")+7).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).substring(1);
					      }
						else if (tempLine.startsWith("ldc2_w"))
						  {
							codeBytes += "14";
							String temp = tempLine.substring(tempLine.indexOf("ldc2_w")+7).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).substring(1);
					      }
						else if (tempLine.startsWith("ldc_w"))
						  {
							codeBytes += "13";
							String temp = tempLine.substring(tempLine.indexOf("ldc_w")+6).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).substring(1);
					      }
						else if (tempLine.startsWith("ldc"))
						  {
							codeBytes += "12";
							String temp = tempLine.substring(tempLine.indexOf("ldc")+4).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFF) + 0x100, 16).substring(1);
					      }
						else if (tempLine.startsWith("iload "))
						  {
							codeBytes += "15";
							String temp = tempLine.substring(tempLine.indexOf("iload")+6).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFF) + 0x100, 16).substring(1);
					      }
						else if (tempLine.startsWith("lload "))
						  {
							codeBytes += "16";
							String temp = tempLine.substring(tempLine.indexOf("lload")+6).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFF) + 0x100, 16).substring(1);
					      }
						else if (tempLine.startsWith("fload "))
						  {
							codeBytes += "17";
							String temp = tempLine.substring(tempLine.indexOf("fload")+6).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFF) + 0x100, 16).substring(1);
					      }
						else if (tempLine.startsWith("dload "))
						  {
							codeBytes += "18";
							String temp = tempLine.substring(tempLine.indexOf("dload")+6).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFF) + 0x100, 16).substring(1);
					      }
						else if (tempLine.startsWith("aload "))
						  {
							codeBytes += "19";
							String temp = tempLine.substring(tempLine.indexOf("aload")+6).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFF) + 0x100, 16).substring(1);
					      }
					    else if (tempLine.startsWith("iload_0"))
							codeBytes += "1A";
						else if (tempLine.startsWith("iload_1"))
							codeBytes += "1B";
						else if (tempLine.startsWith("iload_2"))
							codeBytes += "1C";
						else if (tempLine.startsWith("iload_3"))
							codeBytes += "1D";
						else if (tempLine.startsWith("lload_0"))
							codeBytes += "1E";
						else if (tempLine.startsWith("lload_1"))
							codeBytes += "1F";
						else if (tempLine.startsWith("lload_2"))
							codeBytes += "20";
						else if (tempLine.startsWith("lload_3"))
							codeBytes += "21";
						else if (tempLine.startsWith("fload_0"))
							codeBytes += "22";
						else if (tempLine.startsWith("fload_1"))
							codeBytes += "23";
						else if (tempLine.startsWith("fload_2"))
							codeBytes += "24";
						else if (tempLine.startsWith("fload_3"))
							codeBytes += "25";
						else if (tempLine.startsWith("dload_0"))
							codeBytes += "26";
						else if (tempLine.startsWith("dload_1"))
							codeBytes += "27";
						else if (tempLine.startsWith("dload_2"))
							codeBytes += "28";
						else if (tempLine.startsWith("dload_3"))
							codeBytes += "29";
						else if (tempLine.startsWith("aload_0"))
							codeBytes += "2A";
						else if (tempLine.startsWith("aload_1"))
							codeBytes += "2B";
						else if (tempLine.startsWith("aload_2"))
							codeBytes += "2C";
						else if (tempLine.startsWith("aload_3"))
							codeBytes += "2D";
						else if (tempLine.startsWith("iaload"))
							codeBytes += "2E";
						else if (tempLine.startsWith("laload"))
							codeBytes += "2F";
						else if (tempLine.startsWith("faload"))
							codeBytes += "30";
						else if (tempLine.startsWith("daload"))
							codeBytes += "31";
						else if (tempLine.startsWith("aaload"))
							codeBytes += "32";
						else if (tempLine.startsWith("baload"))
							codeBytes += "33";
						else if (tempLine.startsWith("caload"))
							codeBytes += "34";
						else if (tempLine.startsWith("saload"))
							codeBytes += "35";
						else if (tempLine.startsWith("istore "))
						  {
							codeBytes += "36";
							String temp = tempLine.substring(tempLine.indexOf("istore")+7).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFF) + 0x100, 16).substring(1);
					      }
						else if (tempLine.startsWith("lstore "))
						  {
							codeBytes += "37";
							String temp = tempLine.substring(tempLine.indexOf("lstore")+7).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFF) + 0x100, 16).substring(1);
					      }
						else if (tempLine.startsWith("fstore "))
						  {
							codeBytes += "38";
							String temp = tempLine.substring(tempLine.indexOf("fstore")+7).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFF) + 0x100, 16).substring(1);
					      }
						else if (tempLine.startsWith("dstore "))
						  {
							codeBytes += "39";
							String temp = tempLine.substring(tempLine.indexOf("dstore")+7).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFF) + 0x100, 16).substring(1);
					      }
						else if (tempLine.startsWith("astore "))
						  {
							codeBytes += "3A";
							String temp = tempLine.substring(tempLine.indexOf("astore")+7).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFF) + 0x100, 16).substring(1);
					      }
					    else if (tempLine.startsWith("istore_0"))
							codeBytes += "3B";
						else if (tempLine.startsWith("istore_1"))
							codeBytes += "3C";
						else if (tempLine.startsWith("istore_2"))
							codeBytes += "3D";
						else if (tempLine.startsWith("istore_3"))
							codeBytes += "3E";
						else if (tempLine.startsWith("lstore_0"))
							codeBytes += "3F";
						else if (tempLine.startsWith("lstore_1"))
							codeBytes += "40";
						else if (tempLine.startsWith("lstore_2"))
							codeBytes += "41";
						else if (tempLine.startsWith("lstore_3"))
							codeBytes += "42";
						else if (tempLine.startsWith("fstore_0"))
							codeBytes += "43";
						else if (tempLine.startsWith("fstore_1"))
							codeBytes += "44";
						else if (tempLine.startsWith("fstore_2"))
							codeBytes += "45";
						else if (tempLine.startsWith("fstore_3"))
							codeBytes += "46";
						else if (tempLine.startsWith("dstore_0"))
							codeBytes += "47";
						else if (tempLine.startsWith("dstore_1"))
							codeBytes += "48";
						else if (tempLine.startsWith("dstore_2"))
							codeBytes += "49";
						else if (tempLine.startsWith("dstore_3"))
							codeBytes += "4A";
						else if (tempLine.startsWith("astore_0"))
							codeBytes += "4B";
						else if (tempLine.startsWith("astore_1"))
							codeBytes += "4C";
						else if (tempLine.startsWith("astore_2"))
							codeBytes += "4D";
						else if (tempLine.startsWith("astore_3"))
							codeBytes += "4E";
						else if (tempLine.startsWith("iastore"))
							codeBytes += "4F";
						else if (tempLine.startsWith("lastore"))
							codeBytes += "50";
						else if (tempLine.startsWith("fastore"))
							codeBytes += "51";
						else if (tempLine.startsWith("dastore"))
							codeBytes += "52";
						else if (tempLine.startsWith("aastore"))
							codeBytes += "53";
						else if (tempLine.startsWith("bastore"))
							codeBytes += "54";
						else if (tempLine.startsWith("castore"))
							codeBytes += "55";
						else if (tempLine.startsWith("sastore"))
							codeBytes += "56";
						else if (tempLine.startsWith("pop"))
							codeBytes += "57";
						else if (tempLine.startsWith("pop2"))
							codeBytes += "58";
						else if (tempLine.startsWith("dup"))
							codeBytes += "59";
						else if (tempLine.startsWith("dup_x1"))
							codeBytes += "5A";
						else if (tempLine.startsWith("dup_x2"))
							codeBytes += "5B";
						else if (tempLine.startsWith("dup2"))
							codeBytes += "5C";
						else if (tempLine.startsWith("dup2_x1"))
							codeBytes += "5D";
						else if (tempLine.startsWith("dup2_x2"))
							codeBytes += "5E";
						else if (tempLine.startsWith("swap"))
							codeBytes += "5F";
						else if (tempLine.startsWith("iadd"))
							codeBytes += "60";
						else if (tempLine.startsWith("ladd"))
							codeBytes += "61";
						else if (tempLine.startsWith("fadd"))
							codeBytes += "62";
						else if (tempLine.startsWith("dadd"))
							codeBytes += "63";
						else if (tempLine.startsWith("isub"))
							codeBytes += "64";
						else if (tempLine.startsWith("lsub"))
							codeBytes += "65";
						else if (tempLine.startsWith("fsub"))
							codeBytes += "66";
						else if (tempLine.startsWith("dsub"))
							codeBytes += "67";
						else if (tempLine.startsWith("imul"))
							codeBytes += "68";
						else if (tempLine.startsWith("lmul"))
							codeBytes += "69";
						else if (tempLine.startsWith("fmul"))
							codeBytes += "6A";
						else if (tempLine.startsWith("dmul"))
							codeBytes += "6B";
						else if (tempLine.startsWith("idiv"))
							codeBytes += "6C";
						else if (tempLine.startsWith("ldiv"))
							codeBytes += "6D";
						else if (tempLine.startsWith("fdiv"))
							codeBytes += "6E";
						else if (tempLine.startsWith("ddiv"))
							codeBytes += "6F";
						else if (tempLine.startsWith("irem"))
							codeBytes += "70";
						else if (tempLine.startsWith("lrem"))
							codeBytes += "71";
						else if (tempLine.startsWith("frem"))
							codeBytes += "72";
						else if (tempLine.startsWith("drem"))
							codeBytes += "73";
						else if (tempLine.startsWith("ineg"))
							codeBytes += "74";
						else if (tempLine.startsWith("lneg"))
							codeBytes += "75";
						else if (tempLine.startsWith("fneg"))
							codeBytes += "76";
						else if (tempLine.startsWith("dneg"))
							codeBytes += "77";
						else if (tempLine.startsWith("ishl"))
							codeBytes += "78";
						else if (tempLine.startsWith("lshl"))
							codeBytes += "79";
						else if (tempLine.startsWith("ishr"))
							codeBytes += "7A";
						else if (tempLine.startsWith("lshr"))
							codeBytes += "7B";
						else if (tempLine.startsWith("iushr"))
							codeBytes += "7C";
						else if (tempLine.startsWith("lushr"))
							codeBytes += "7D";
						else if (tempLine.startsWith("iand"))
							codeBytes += "7E";
						else if (tempLine.startsWith("land"))
							codeBytes += "7F";
						else if (tempLine.startsWith("ior"))
							codeBytes += "80";
						else if (tempLine.startsWith("lor"))
							codeBytes += "81";
						else if (tempLine.startsWith("ixor"))
							codeBytes += "82";
						else if (tempLine.startsWith("lxor"))
							codeBytes += "83";
						else if (tempLine.startsWith("iinc "))
						  {
							codeBytes += "84";
							String temp = tempLine.substring(tempLine.indexOf("iinc")+5).trim();
							tempNum = Integer.parseInt(temp.substring(0,temp.indexOf(" ")));
							codeBytes += Integer.toString((tempNum&0xFF) + 0x100, 16).substring(1);
							tempNum = Integer.parseInt(temp.substring(temp.indexOf(" ")+1));
							codeBytes += Integer.toString((tempNum&0xFF) + 0x100, 16).substring(1);
					      }
						else if (tempLine.startsWith("i2l"))
							codeBytes += "85";
						else if (tempLine.startsWith("i2f"))
							codeBytes += "86";
						else if (tempLine.startsWith("i2d"))
							codeBytes += "87";
						else if (tempLine.startsWith("l2i"))
							codeBytes += "88";
						else if (tempLine.startsWith("l2f"))
							codeBytes += "89";
						else if (tempLine.startsWith("l2d"))
							codeBytes += "8A";
						else if (tempLine.startsWith("f2i"))
							codeBytes += "8B";
						else if (tempLine.startsWith("f2l"))
							codeBytes += "8C";
						else if (tempLine.startsWith("f2d"))
							codeBytes += "8D";
						else if (tempLine.startsWith("d2i"))
							codeBytes += "8E";
						else if (tempLine.startsWith("d2l"))
							codeBytes += "8F";
						else if (tempLine.startsWith("d2f"))
							codeBytes += "90";
						else if (tempLine.startsWith("i2b"))
							codeBytes += "91";
						else if (tempLine.startsWith("i2c"))
							codeBytes += "92";
						else if (tempLine.startsWith("i2s"))
							codeBytes += "93";
						else if (tempLine.startsWith("lcmp"))
							codeBytes += "94";
						else if (tempLine.startsWith("fcmpl"))
							codeBytes += "95";
						else if (tempLine.startsWith("fcmpg"))
							codeBytes += "96";
						else if (tempLine.startsWith("dcmpl"))
							codeBytes += "97";
						else if (tempLine.startsWith("dcmpg"))
							codeBytes += "98";
						else if (tempLine.startsWith("ifeq "))
						  {
							codeBytes += "99";
							String temp = tempLine.substring(tempLine.indexOf("ifeq")+5).trim();
							tempNum = Integer.parseInt(temp)-lineNum;
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).toUpperCase().substring(1);
					      }
						else if (tempLine.startsWith("ifne "))
						  {
							codeBytes += "9A";
							String temp = tempLine.substring(tempLine.indexOf("ifne")+5).trim();
							tempNum = Integer.parseInt(temp)-lineNum;
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).toUpperCase().substring(1);
					      }
						else if (tempLine.startsWith("iflt "))
						  {
							codeBytes += "9B";
							String temp = tempLine.substring(tempLine.indexOf("iflt")+5).trim();
							tempNum = Integer.parseInt(temp)-lineNum;
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).toUpperCase().substring(1);
					      }
						else if (tempLine.startsWith("ifge "))
						  {
							codeBytes += "9C";
							String temp = tempLine.substring(tempLine.indexOf("ifne")+5).trim();
							tempNum = Integer.parseInt(temp)-lineNum;
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).toUpperCase().substring(1);
					      }
						else if (tempLine.startsWith("ifgt "))
						  {
							codeBytes += "9D";
							String temp = tempLine.substring(tempLine.indexOf("iflt")+5).trim();
							tempNum = Integer.parseInt(temp)-lineNum;
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).toUpperCase().substring(1);
					      }
						else if (tempLine.startsWith("ifle "))
						  {
							codeBytes += "9E";
							String temp = tempLine.substring(tempLine.indexOf("ifne")+5).trim();
							tempNum = Integer.parseInt(temp)-lineNum;
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).toUpperCase().substring(1);
					      }
						else if (tempLine.startsWith("if_icmpeq "))
						  {
							codeBytes += "9F";
							String temp = tempLine.substring(tempLine.indexOf("if_icmpeq")+10).trim();
							tempNum = Integer.parseInt(temp)-lineNum;
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).toUpperCase().substring(1);
					      }
						else if (tempLine.startsWith("if_icmpne "))
						  {
							codeBytes += "A0";
							String temp = tempLine.substring(tempLine.indexOf("if_icmpne")+10).trim();
							tempNum = Integer.parseInt(temp)-lineNum;
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).toUpperCase().substring(1);
					      }
						else if (tempLine.startsWith("if_icmplt "))
						  {
							codeBytes += "A1";
							String temp = tempLine.substring(tempLine.indexOf("if_icmplt")+10).trim();
							tempNum = Integer.parseInt(temp)-lineNum;
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).toUpperCase().substring(1);
					      }
						else if (tempLine.startsWith("if_icmpge "))
						  {
							codeBytes += "A2";
							String temp = tempLine.substring(tempLine.indexOf("if_icmpge")+10).trim();
							tempNum = Integer.parseInt(temp)-lineNum;
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).toUpperCase().substring(1);
					      }
						else if (tempLine.startsWith("if_icmpgt "))
						  {
							codeBytes += "A3";
							String temp = tempLine.substring(tempLine.indexOf("if_icmpgt")+10).trim();
							tempNum = Integer.parseInt(temp)-lineNum;
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).toUpperCase().substring(1);
					      }
						else if (tempLine.startsWith("if_icmple "))
						  {
							codeBytes += "A4";
							String temp = tempLine.substring(tempLine.indexOf("if_icmple")+10).trim();
							tempNum = Integer.parseInt(temp)-lineNum;
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).toUpperCase().substring(1);
					      }
						else if (tempLine.startsWith("if_acmpeq "))
						  {
							codeBytes += "A5";
							String temp = tempLine.substring(tempLine.indexOf("if_acmpeq")+10).trim();
							tempNum = Integer.parseInt(temp)-lineNum;
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).toUpperCase().substring(1);
					      }
						else if (tempLine.startsWith("if_acmpne "))
						  {
							codeBytes += "A6";
							String temp = tempLine.substring(tempLine.indexOf("if_acmpne")+10).trim();
							tempNum = Integer.parseInt(temp)-lineNum;
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).toUpperCase().substring(1);
					      }
						else if (tempLine.startsWith("goto "))
						  {
							codeBytes += "A7";
							String temp = tempLine.substring(tempLine.indexOf("goto")+5).trim();
							tempNum = Integer.parseInt(temp)-lineNum;
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).toUpperCase().substring(1);
					      }
						else if (tempLine.startsWith("jsr "))
						  {
							codeBytes += "A8";
							String temp = tempLine.substring(tempLine.indexOf("jsr")+4).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).substring(1);
					      }
						else if (tempLine.startsWith("ret "))
						  {
							codeBytes += "A9";
							String temp = tempLine.substring(tempLine.indexOf("ret")+4).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).substring(1);
					      }
					    else if (tempLine.startsWith("tableswitch"))
					      {
							codeBytes += "AA";
							long lownum, hinum;
							String tempToHex; //Used for correcting the length of hex strings

							//Do padding
							if (DebugMode)
							   System.out.println("Code length: "+ codeBytes.length()/2);
							for (int num=(4-((codeBytes.length()/2)%4))%4; num>0; num--)
							{
								codeBytes += "00";
							}


							//Get default branch
							String temp = tempLine.substring(tempLine.indexOf("default:")+9).trim();
							tempLine = tempLine.substring(0, tempLine.indexOf("default")-1);
							if (DebugMode)
								System.out.println("Default tableswitch branch: " + temp);
							long lTempNum = Long.parseLong(temp)-lineNum;
							tempToHex = Long.toHexString(lTempNum);
							while (tempToHex.length() < 8)
							    tempToHex = "0" + tempToHex;
							codeBytes += tempToHex;

							//Get lownum for comparison
							temp = tempLine.substring(tempLine.indexOf("tableswitch")+11,tempLine.indexOf(":")).trim();
							if (DebugMode)
								System.out.println("Tableswitch LOWNUM: " + temp);
							lTempNum = Long.parseLong(temp);
							lownum = lTempNum;
							tempToHex = Long.toHexString(lTempNum);
							while (tempToHex.length() < 8)
							    tempToHex = "0" + tempToHex;
							codeBytes += tempToHex;

							//Get hinum for comparison
							tempLine = tempLine.substring(tempLine.indexOf(":")+2);
							temp = tempLine.substring(0,tempLine.indexOf(" ")).trim();
							if (DebugMode)
								System.out.println("Default tableswitch HINUM: " + temp);
							lTempNum = Long.parseLong(temp);
							hinum = lTempNum;
							tempToHex = Long.toHexString(lTempNum);
							while (tempToHex.length() < 8)
							    tempToHex = "0" + tempToHex;
							codeBytes += tempToHex;

							//Get branch points
							for (int k = (int)(hinum-lownum); k>=0; k--)
							{
								tempLine = tempLine.trim();
								tempLine = tempLine.substring(tempLine.indexOf(" ")+1);
								if (DebugMode)
									System.out.println(tempLine);

								temp = tempLine.substring(0,tempLine.indexOf(":")).trim();
								if (DebugMode)
									System.out.println("Comp Number: " + temp);

								tempLine = tempLine.substring(tempLine.indexOf(":")+2);

								if (tempLine.indexOf(" ") != -1)
									temp = tempLine.substring(0,tempLine.indexOf(" ")).trim();
								else
									temp = tempLine.trim();
								if (DebugMode)
									System.out.println("Branch to: " + temp);
								lTempNum = Long.parseLong(temp)-lineNum;
								tempToHex = Long.toHexString(lTempNum);
								while (tempToHex.length() < 8)
									tempToHex = "0" + tempToHex;
								codeBytes += tempToHex;
							}
					  	  }
					    else if (tempLine.startsWith("lookupswitch"))
					      {
							codeBytes += "AB";
							long numcases;
							String tempToHex; //Used for correcting the length of hex strings

							//Do padding
							if (DebugMode)
							   System.out.println("Code length: "+ codeBytes.length());
							for (int num=(4-((codeBytes.length()/2)%4))%4; num>0; num--)
							{
								codeBytes += "00";
							}


							//Get default branch
							String temp = tempLine.substring(tempLine.indexOf("default:")+9).trim();
							tempLine = tempLine.substring(0, tempLine.indexOf("default")-1);
							if (DebugMode)
								System.out.println("Default lookupswitch branch: " + temp);
							long lTempNum = Long.parseLong(temp)-lineNum;
							tempToHex = Long.toHexString(lTempNum);
							while (tempToHex.length() < 8)
							    tempToHex = "0" + tempToHex;
							codeBytes += tempToHex;

							//Get lownum for comparison
							tempLine = tempLine.substring(tempLine.indexOf("lookupswitch")+13).trim();
							temp = tempLine.substring(0,tempLine.indexOf(" ")).trim();
							if (DebugMode)
								System.out.println("Number of cases: " + temp);
							lTempNum = Long.parseLong(temp);
							numcases = lTempNum;
							tempToHex = Long.toHexString(lTempNum);
							while (tempToHex.length() < 8)
							    tempToHex = "0" + tempToHex;
							codeBytes += tempToHex;

							//Get branch points
							for (int k = (int)numcases; k>0; k--)
							{
								tempLine = tempLine.trim();
								tempLine = tempLine.substring(tempLine.indexOf(" ")+1);

								temp = tempLine.substring(0,tempLine.indexOf(":")).trim();
								if (DebugMode)
									System.out.println("Comp Number: " + temp);
								lTempNum = Long.parseLong(temp);
								tempToHex = Long.toHexString(lTempNum);
								while (tempToHex.length() < 8)
									tempToHex = "0" + tempToHex;
								codeBytes += tempToHex;

								tempLine = tempLine.substring(tempLine.indexOf(":")+2);

								if (tempLine.indexOf(" ") != -1)
									temp = tempLine.substring(0,tempLine.indexOf(" ")).trim();
								else
									temp = tempLine.trim();
								if (DebugMode)
									System.out.println("Branch to: " + temp);
								lTempNum = Long.parseLong(temp)-lineNum;
								tempToHex = Long.toHexString(lTempNum);
								while (tempToHex.length() < 8)
									tempToHex = "0" + tempToHex;
								codeBytes += tempToHex;
							}
					  	  }
						else if (tempLine.startsWith("ireturn"))
							codeBytes += "AC";
						else if (tempLine.startsWith("lreturn"))
							codeBytes += "AD";
						else if (tempLine.startsWith("freturn"))
							codeBytes += "AE";
						else if (tempLine.startsWith("dreturn"))
							codeBytes += "AF";
						else if (tempLine.startsWith("areturn"))
							codeBytes += "B0";
						else if (tempLine.startsWith("return"))
							codeBytes += "B1";
						else if (tempLine.startsWith("getstatic"))
						  {
							codeBytes += "B2";
							String temp = tempLine.substring(tempLine.indexOf("getstatic")+10).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).substring(1);
					      }
						else if (tempLine.startsWith("putstatic"))
						  {
							codeBytes += "B3";
							String temp = tempLine.substring(tempLine.indexOf("putstatic")+10).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).substring(1);
					      }
						else if (tempLine.startsWith("getfield"))
						  {
							codeBytes += "B4";
							String temp = tempLine.substring(tempLine.indexOf("getfield")+9).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).substring(1);
					      }
						else if (tempLine.startsWith("putfield"))
						  {
							codeBytes += "B5";
							String temp = tempLine.substring(tempLine.indexOf("putfield")+9).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).substring(1);
					      }
						else if (tempLine.startsWith("invokevirtual"))
						  {
							codeBytes += "B6";
							String temp = tempLine.substring(tempLine.indexOf("invokevirtual")+14).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).substring(1);
					      }
						else if (tempLine.startsWith("invokespecial"))
						  {
							codeBytes += "B7";
							String temp = tempLine.substring(tempLine.indexOf("invokespecial")+14).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).substring(1);
					      }
						else if (tempLine.startsWith("invokestatic"))
						  {
							codeBytes += "B8";
							String temp = tempLine.substring(tempLine.indexOf("invokespecial")+13).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).substring(1);
					      }
						else if (tempLine.startsWith("invokeinterface"))
						  {
							codeBytes += "B9";
							String temp = tempLine.substring(tempLine.indexOf("invokeinterface")+16).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).substring(1);
					      }

					    //BA is unused.

						else if (tempLine.startsWith("new "))
						  {
							codeBytes += "BB";
							String temp = tempLine.substring(tempLine.indexOf("new")+4).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).substring(1);
					      }
						else if (tempLine.startsWith("newarray "))
						  {
							codeBytes += "BC";
							String temp = tempLine.substring(tempLine.indexOf("newarray")+9).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFF) + 0x100, 16).substring(1);
					      }
						else if (tempLine.startsWith("anewarray"))
						  {
							codeBytes += "BD";
							String temp = tempLine.substring(tempLine.indexOf("anewarray")+10).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).substring(1);
					      }
						else if (tempLine.startsWith("arraylength"))
							codeBytes += "BE";
						else if (tempLine.startsWith("athrow"))
							codeBytes += "BF";
						else if (tempLine.startsWith("checkcast"))
						  {
							codeBytes += "C0";
							String temp = tempLine.substring(tempLine.indexOf("checkcast")+10).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).substring(1);
					      }
						else if (tempLine.startsWith("instanceof"))
						  {
							codeBytes += "C1";
							String temp = tempLine.substring(tempLine.indexOf("instanceof")+11).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).substring(1);
					      }
						else if (tempLine.startsWith("monitorenter"))
							codeBytes += "C2";
						else if (tempLine.startsWith("monitorexit"))
							codeBytes += "C3";
						else if (tempLine.startsWith("wide"))
						  {
							codeBytes += "C4";
							String temp = tempLine.substring(tempLine.indexOf("wide")+5).trim();
							tempNum = Integer.parseInt(temp);
							tempNum = Integer.parseInt(temp.substring(0,temp.indexOf(" ")));
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).substring(1);
							tempNum = Integer.parseInt(temp.substring(temp.indexOf(" ")+1));
							codeBytes += Integer.toString((tempNum&0xFF) + 0x100, 16).substring(1);
					      }
						else if (tempLine.startsWith("multianewarray"))
						  {
							codeBytes += "C5";
							String temp = tempLine.substring(tempLine.indexOf("multianewarray")+15).trim();
							tempNum = Integer.parseInt(temp.substring(0,temp.indexOf(" ")));
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).substring(1);
							tempNum = Integer.parseInt(temp.substring(temp.indexOf(" ")+1));
							codeBytes += Integer.toString((tempNum&0xFF) + 0x100, 16).substring(1);
					      }
						else if (tempLine.startsWith("ifnull"))
						  {
							codeBytes += "C6";
							String temp = tempLine.substring(tempLine.indexOf("ifnull")+7).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).substring(1);
					      }
						else if (tempLine.startsWith("ifnonnull"))
						  {
							codeBytes += "C7";
							String temp = tempLine.substring(tempLine.indexOf("ifnonnull")+10).trim();
							tempNum = Integer.parseInt(temp);
							codeBytes += Integer.toString((tempNum&0xFFFF) + 0x10000, 16).substring(1);
					      }
						else if (tempLine.startsWith("goto_w"))
						  {
							codeBytes += "C8";
							String temp = tempLine.substring(tempLine.indexOf("goto_w")+7).trim();
							long tempLong = Long.parseLong(temp);
							codeBytes += Long.toString((tempNum&0xFFFFFFFF) + 0x00000000, 16).substring(1);
					      }
						else if (tempLine.startsWith("jsr_w"))
						  {
							codeBytes += "C9";
							String temp = tempLine.substring(tempLine.indexOf("jsr_w")+6).trim();
							long tempLong = Long.parseLong(temp);
							codeBytes += Long.toString((tempNum&0xFFFFFFFF) + 0x00000000, 16).substring(1);
					      }
						else if (tempLine.startsWith("breakpoint"))
							codeBytes += "CA";
						else if (tempLine.startsWith("impdep1"))
							codeBytes += "FE";
						else if (tempLine.startsWith("impdep2"))
							codeBytes += "FF";
						else
							System.out.println("Instruction error!" + tempLine);
						}
					if (DebugMode)
						System.out.println(codeBytes);

					int codeBytesLen = codeBytes.length();
					for (int j = 0; j < codeBytesLen; j+=2) {
						out.writeByte(hexToDecimal(codeBytes.substring(j,j+2)));
						if (codeBytes.length()==1);
					}
					if (DebugMode)
						System.out.println(out.size());

					//exception table length
					int excepLength = Integer.valueOf(attrInfo.getElementsByTagName("ExceptionTable_Length").item(0).getChildNodes().item(0).getNodeValue()).intValue();
					out.writeShort(excepLength);
					if (DebugMode)
						System.out.println("Exception Tables length: " + excepLength);

					//exception table(s)
					if (excepLength>0) {
						NodeList excepAttrs = attrInfo.getElementsByTagName("ExceptionTables").item(0).getChildNodes();
						//Element excepAttrs = (Element) attrInfo.getElementsByTagName("ExceptionTable").item(0);
						for (int k = 1; k <= excepLength*2; k+=2) {
							Element excepAttr = (Element) excepAttrs.item(k);
							if (DebugMode)
								System.out.println("Exception Table attribute: " + excepAttr);
							out.writeShort(Integer.valueOf(excepAttr.getElementsByTagName("StartPC").item(0).getChildNodes().item(0).getNodeValue().trim()).intValue());
							out.writeShort(Integer.valueOf(excepAttr.getElementsByTagName("EndPC").item(0).getChildNodes().item(0).getNodeValue().trim()).intValue());
							out.writeShort(Integer.valueOf(excepAttr.getElementsByTagName("HandlerPC").item(0).getChildNodes().item(0).getNodeValue().trim()).intValue());
							out.writeShort(Integer.valueOf(excepAttr.getElementsByTagName("CatchType").item(0).getChildNodes().item(0).getNodeValue().trim()).intValue());
						}
					}

					//attributes count
					int codeAttrLength = Integer.valueOf(attrInfo.getElementsByTagName("CodeAttribute_Count").item(0).getChildNodes().item(0).getNodeValue()).intValue();
					out.writeShort(codeAttrLength);
					out.writeShort(Integer.valueOf(attrInfo.getElementsByTagName("CodeAttribute_Name_Index").item(0).getChildNodes().item(0).getNodeValue()).intValue());
					out.writeInt(Integer.valueOf(attrInfo.getElementsByTagName("CodeAttribute_Length").item(0).getChildNodes().item(0).getNodeValue()).intValue());
					int lntLength = Integer.valueOf(attrInfo.getElementsByTagName("LineNumTable_Count").item(0).getChildNodes().item(0).getNodeValue()).intValue();
					out.writeShort(lntLength);
					lntLength = lntLength*2 + 1;
					if (lntLength>0) {
						NodeList lntAttrs = attrInfo.getElementsByTagName("LineNumTable").item(0).getChildNodes();
						for (int k = 1; k < lntLength; k+=2) {
							Element lntAttr = (Element) lntAttrs.item(k);
							out.writeShort(Integer.valueOf(lntAttr.getElementsByTagName("StartPC").item(0).getChildNodes().item(0).getNodeValue()).intValue());
							out.writeShort(Integer.valueOf(lntAttr.getElementsByTagName("LineNum").item(0).getChildNodes().item(0).getNodeValue()).intValue());
						}
					}

					//attributes
					/*if (codeAttrLength > 0) {
						NodeList codeAttrs = attrInfo.getElementsByTagName("Code_Attributes").item(0).getChildNodes();
						for (int l = 0; l < codeAttrLength; l++) {
							Element codeAttr = (Element) codeAttrs.item(l);
							out.writeShort(Integer.valueOf(attrInfo.getElementsByTagName("Code_Attribute_Name_Index").item(0).getChildNodes().item(0).getNodeValue()).intValue());
							out.writeInt(Integer.valueOf(attrInfo.getElementsByTagName("Code_Attribute_Length").item(0).getChildNodes().item(0).getNodeValue()).intValue());
							int codeAttrTableLen = Integer.valueOf(attrInfo.getElementsByTagName("Code_Attribute_Table_Length").item(0).getChildNodes().item(0).getNodeValue()).intValue();
							out.writeShort(codeAttrTableLen);
							NodeList codeAttrTables = codeAttr.getElementsByTagName("Code_Attributes_Tables").item(0).getChildNodes();
							for (int m = 0; m < codeAttrTableLen; m++) {
								Element codeAttrTable = (Element) codeAttrTables.item(m);
								String tableType = codeAttrTable.getNodeName();
								if (tableType.equals("LineNumberTable")) {
									out.writeShort(Integer.valueOf(codeAttrTable.getElementsByTagName("StartPC").item(0).getChildNodes().item(0).getNodeValue()).intValue());
									out.writeShort(Integer.valueOf(codeAttrTable.getElementsByTagName("LineNbr").item(0).getChildNodes().item(0).getNodeValue()).intValue());
								}
								if (tableType.equals("LocalVariableTable")) {
									out.writeShort(Integer.valueOf(codeAttrTable.getElementsByTagName("StartPC").item(0).getChildNodes().item(0).getNodeValue()).intValue());
									out.writeShort(Integer.valueOf(codeAttrTable.getElementsByTagName("Length").item(0).getChildNodes().item(0).getNodeValue()).intValue());
									out.writeShort(Integer.valueOf(codeAttrTable.getElementsByTagName("Name_Index").item(0).getChildNodes().item(0).getNodeValue()).intValue());
									out.writeShort(Integer.valueOf(codeAttrTable.getElementsByTagName("Descriptor_Index").item(0).getChildNodes().item(0).getNodeValue()).intValue());
									out.writeShort(Integer.valueOf(codeAttrTable.getElementsByTagName("Index").item(0).getChildNodes().item(0).getNodeValue()).intValue());
								}
							}
						}*/
				}

				//constant value
				if (attrType.equals("ConstantValue")) {
					int constValueIndex = Integer.valueOf(attrInfo.getElementsByTagName("Constant_Value_Index").item(0).getChildNodes().item(0).getNodeValue()).intValue();
					out.writeShort(constValueIndex);
				}

				//inner classes
				if (attrType.equals("InnerClasses")) {
					int innerClassesCount = Integer.valueOf(attrInfo.getElementsByTagName("Inner_Classes_Count").item(0).getChildNodes().item(0).getNodeValue()).intValue();
					out.writeShort(innerClassesCount);
					NodeList innerClasses = attrInfo.getElementsByTagName("Inner_Classes").item(0).getChildNodes();
					for (int n = 0; n < innerClassesCount; n++) {
						Element innerClass = (Element) innerClasses.item(n);
						out.writeShort(Integer.valueOf(innerClass.getElementsByTagName("Inner_Class_Info_Index").item(0).getChildNodes().item(0).getNodeValue()).intValue());
						out.writeShort(Integer.valueOf(innerClass.getElementsByTagName("Outer_Class_Info_Index").item(0).getChildNodes().item(0).getNodeValue()).intValue());
						out.writeShort(Integer.valueOf(innerClass.getElementsByTagName("Inner_Name_Index").item(0).getChildNodes().item(0).getNodeValue()).intValue());
						String hexAccessFlag = innerClass.getElementsByTagName("Access_Flags").item(0).getChildNodes().item(0).getNodeValue();
						String decimalAccessFlag = hexAccessFlag.substring(2,hexAccessFlag.length());
			    		Integer intAccessFlag = new Integer(hexToDecimal(decimalAccessFlag));
			         	out.writeShort(intAccessFlag.shortValue());
			         }
				}

				//source file
				if (attrType.equals("SourceFile")) {
					int sourceFileIndex = Integer.valueOf(attrInfo.getElementsByTagName("Source_File_Index").item(0).getChildNodes().item(0).getNodeValue().trim()).intValue();
					out.writeShort(sourceFileIndex);
				}

				//synthetic
				//  nothing else to do

			}

		} catch (IOException e) {
			System.err.println("File cannot be read:" + e.getMessage());
		} catch (DOMException e) {
			System.err.println("DOM exception:" + e.getMessage());
		}
	}


	//
	//helper methods
	//
	static int hexToDecimal(String hexValue) {
	    int number = Integer.parseInt(hexValue,16);
	    return number;
	}


	static String getNodeValue(Document doc, String nodeName) {
		String val = "";
		Node targetNode = doc.getElementsByTagName(nodeName).item(0);
		if (targetNode.getChildNodes().getLength() > 0) {
			val = (String) targetNode.getChildNodes().item(0).getNodeValue();
		}
		return val;
	}

} //end of XMLToClass class

