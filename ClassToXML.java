import java.io.*;


class ClassParser {
	String[][] constantPool;
	int numEntries;

	void printCafeBabe (RandomAccessFile iput) {
		// read in magic number and major/minor versions
		try {
			System.out.println("<MagicNumber>");
			System.out.println("0x" + Integer.toString(iput.readUnsignedShort(),16)
									+ Integer.toString(iput.readUnsignedShort(),16));
			System.out.println("</MagicNumber>");

			System.out.println("<MinorVersion>");
			System.out.println(iput.readShort());
			System.out.println("</MinorVersion>");
			System.out.println("<MajorVersion>");
			System.out.println(iput.readShort());
			System.out.println("</MajorVersion>");
		}catch (Exception e) {
        	System.err.println("File read error:" + e.getMessage());
		}
	}


	void getConstantPool (RandomAccessFile iput) {
		int tag, strSize;
		char c;
		StringBuffer Buff = new StringBuffer();

		try {
			numEntries = iput.readShort();
			//System.out.println("<ConstantPool_Count>" + numEntries + "</ConstantPool_Count>");
			constantPool = new String[numEntries][2];  // ignore first entry

			outer:
			for(int i = 1; i < numEntries; i++) {
				tag = iput.readByte();
				//System.out.println(tag);
				switch(tag) {
					case 1: constantPool[i][0] = "CONSTANT_Utf8";
							Buff.setLength(0);
							Buff.append("CONSTVAL");
							strSize = iput.readShort();
							while (strSize > 0) {
								c = (char) (iput.readByte());
								if (c == ' ')
								   Buff.append('\\');
								if (c == '<')
								   Buff.append("&lt;");
								else if (c == '>')
								   Buff.append("&gt;");
								else if (c>31)
								   Buff.append(c);
								strSize--;
								//if (c < 32)
								 //  break;
							}
							//if (c<32
							constantPool[i][1] = Buff.toString();
							//System.out.println(constantPool[i][1]);
							break;
					case 3: constantPool[i][0] = "CONSTANT_Integer";
							constantPool[i][1] = Integer.toString(iput.readInt()); break;
					case 4: constantPool[i][0] = "CONSTANT_Float";
							constantPool[i][1] = Float.toString(iput.readFloat()); break;
					case 5: constantPool[i][0] = "CONSTANT_Long";
							constantPool[i][1] = Long.toString(iput.readLong());
							i++;
							constantPool[i][0] = "CONSTANT_Long";
							constantPool[i][1] = constantPool[i-1][1]; break;
					case 6: constantPool[i][0] = "CONSTANT_Double";
							constantPool[i][1] = Double.toString(iput.readDouble());
							i++; constantPool[i][0] = "CONSTANT_Double";
							constantPool[i][1] = constantPool[i-1][1];  break;
					case 7: constantPool[i][0] = "CONSTANT_Class";
							constantPool[i][1] = Integer.toString(iput.readShort()); break;
					case 8: constantPool[i][0] = "CONSTANT_String";
							constantPool[i][1] = Integer.toString(iput.readShort()); break;
					case 9: constantPool[i][0] = "CONSTANT_Fieldref";
							constantPool[i][1] = Integer.toString(iput.readShort()) + "," + Integer.toString(iput.readShort()); break;
					case 10: constantPool[i][0] = "CONSTANT_Methodref";
							 constantPool[i][1] = Integer.toString(iput.readShort()) + "," + Integer.toString(iput.readShort()); break;
					case 11: constantPool[i][0] = "CONSTANT_InterfaceMethodRef";
							 constantPool[i][1] = Integer.toString(iput.readShort()) + "," + Integer.toString(iput.readShort()); break;
					case 12: constantPool[i][0] = "CONSTANT_NameAndType";
							 constantPool[i][1] = Integer.toString(iput.readShort()) + "," + Integer.toString(iput.readShort()); break;
					default: System.out.println("where do you think you're going"); break outer;
				}
			}
		}catch (Exception e) {
        	System.err.println("File read error:" + e.getMessage());
		}
	}

	String getByteChunkAsString(RandomAccessFile iput) {
		StringBuffer Buff = new StringBuffer();
		int unsignedByte;

		Buff.setLength(0);
		try {
			unsignedByte = iput.readUnsignedByte();
			if (unsignedByte == 0 ){
				Buff.append("00");
			} else {
				if ((Integer.toHexString(unsignedByte)).length() == 1) {
						Buff.append("0");
				}
				Buff.append(Integer.toHexString(unsignedByte));
			}

		}catch (Exception e) {
		    System.err.println("File read error:" + e.getMessage());
		}
		return Buff.toString();
	}


	String getByteChunkAsNumber(RandomAccessFile iput, int count, int codeIdx) {
		StringBuffer Buff = new StringBuffer();
		String tmpStr;
		int unsignedByte;
		int val;

		Buff.setLength(0);
		for (int i=0; i<count; i++) {
			try {
				unsignedByte = iput.readUnsignedByte();
				if (unsignedByte == 0 ){
					Buff.append("00");
				} else {
					if ((Integer.toHexString(unsignedByte)).length() == 1) {
							Buff.append("0");
					}
					Buff.append(Integer.toHexString(unsignedByte));
				}
			}catch (Exception e) {
		    	System.err.println("File read error:" + e.getMessage());
			}
		}
		tmpStr = Buff.toString();
		val = Integer.parseInt(tmpStr, 16) + codeIdx;
		// val = Integer.parseInt(tmpStr, 16);
		return (String.valueOf(val));
	}


	String getAccessFlags(int typecode) {
		String StringClassAccess ="";

		if ((typecode & 1) == 1)
		   StringClassAccess = "public ";
		else if ((typecode & 2) == 2)
				   StringClassAccess = "private ";
		else if ((typecode & 4) == 4)
				   StringClassAccess = "protected ";

		if ((typecode & 8) == 8)
		   StringClassAccess += "static ";
		if ((typecode & 0x10) == 0x10)
		   StringClassAccess += "final ";
		if ((typecode & 0x40) ==  0x40)
		   StringClassAccess += "volatile ";
		if ((typecode & 0x200) == 0x200)
		   StringClassAccess += "interface ";
		if ((typecode & 0x400) == 0x400)
		   StringClassAccess += "abstract ";

		return StringClassAccess;
	}


	void getFields(RandomAccessFile iput) {
		int attCount;

		try {
			numEntries = iput.readShort();

			System.out.println("<Field_Count>" + numEntries + "</Field_Count>");
			System.out.println("<Fields>");

			for(int i = 0; i < numEntries; i++) {
				System.out.println("<Field>");
				System.out.println("<AccessFlags>" + getAccessFlags((int) iput.readShort()) + "</AccessFlags>");
				System.out.println("<Name_Index>" + iput.readShort() + "</Name_Index>");
				System.out.println("<Description_Index>" + iput.readShort() + "</Description_Index>");
				attCount = iput.readShort();
				System.out.println("<Attribute_Count>" + attCount + "</Attribute_Count>");
				System.out.println("<Attributes>");
				// read in the fieldname attributes
				for(int j=0; j < attCount; j++) {
					getAttributes(iput);
				}
				System.out.println("</Attributes>");
				System.out.println("</Field>");
			}
		    System.out.println("</Fields>");
		}catch (Exception e) {
		    System.err.println("File read error:" + e.getMessage());
		}
	}

	void getMethods(RandomAccessFile iput) {
		int attCount, tempInt, MethName=0, MethParam=0;
		String temp;

		try
		{
			numEntries = iput.readShort();
			System.out.println("<Method_Count>" + numEntries + "</Method_Count>");
			System.out.println("<Methods>");
			for(int i = 1; i <= numEntries; i++)
			{
				System.out.println("<Method>");
				System.out.println("<AccessFlags>");
				tempInt=iput.readShort();
				System.out.print(getAccessFlags(tempInt));
				System.out.println("</AccessFlags>");

				System.out.println("<Name_Index>");
				MethName = iput.readShort();
				System.out.println(MethName);
				System.out.println("</Name_Index>");
				System.out.println("<Description_Index>");
				MethParam = iput.readShort();
				System.out.println(MethParam);
				System.out.println("</Description_Index>");

				attCount = iput.readShort();
				System.out.println("<Attribute_Count>");
				System.out.println(attCount);
				System.out.println("</Attribute_Count>");


				// read in the method attributes
				System.out.println("<Attributes>");
				for(int j=0; j < attCount; j++)
				{
					getAttributes(iput);
				}
				System.out.println("</Attributes>");
				System.out.println("</Method>");
			}
			System.out.println("</Methods>");
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			System.err.println("Caught ArrayIndexOutOfBoundsException: " +
				e.getMessage());
		}
		catch (IOException e)
		{
			System.err.println("Caught IOException: " +
				e.getMessage());
		}
		catch (Exception e)
		{
			System.err.println("File read error:" + e.getMessage());
		}
	}



	void getAttributes(RandomAccessFile ip) {
		StringBuffer Buff = new StringBuffer();
		String attType;
		String chunk;
		int attTypeIndex, attLen, codeLen, codeLenMax, numExcept, numLines, numInner;

		try
		{
			System.out.println("<Attribute>");
			// Code, InnerClasses, ConstantValue, SourceFile, Exceptions, and Synthetic
			attTypeIndex = ip.readShort();
			attType = constantPool[attTypeIndex][1].substring(8);
			System.out.println("<Attribute_Type>" + attType + "</Attribute_Type>");
			System.out.println("<Attribute_Type_Index>" + attTypeIndex + "</Attribute_Type_Index>");
			System.out.println("<Attribute_Length>" + ip.readInt() + "</Attribute_Length>");

			// decide how to process depending on attType
			if(attType.equals("ConstantValue"))
			{
				System.out.println("<Constant_Value_Index>" + ip.readShort() + "</Constant_Value_Index>");
			}
			else if (attType.equals("Synthetic"))
			{
			}
			else if (attType.equals("SourceFile"))
			{
				System.out.println("<Source_File_Index> " + ip.readShort() + " </Source_File_Index>");
			}
			else if (attType.equals("InnerClasses"))
			{
				numInner = ip.readShort();
				System.out.println("<InnerClassesNum>" + numInner + "</InnerClassesNum>");
				System.out.println("<InnerClasses>");
				for(int i=1;i <= numInner; i++)
				{
					System.out.println("<InnerClass>");
					System.out.println("<InnerClassIndex>" + ip.readShort() + "</InnerClassIndex>");
					System.out.println("<OuterClassIndex>" + ip.readShort() + "</OuterClassIndex>");
					System.out.println("<InnerClassName>" + ip.readShort() + "</InnerClassName>");
					System.out.println("<AccessFlags> 0x" + getByteChunkAsString(ip) + getByteChunkAsString(ip) + " </AccessFlags>");
					System.out.println("</InnerClass>");
				}
				System.out.println("</InnerClasses>");
			}
			else if (attType.equals("Exceptions"))
			{
				numExcept = ip.readShort();
				System.out.println("<ExceptionsNum>" + numExcept + "</ExceptionsNum>");
				System.out.println("<Exceptions>");
				for(int i=1;i <= numExcept; i++)
				{
					System.out.println("<Exception>" + ip.readShort() + "</Exception>");
				}
				System.out.println("</Exceptions>");
			}
			else if (attType.equals("Code"))
			{
				int pc, temp, attCount;
				boolean useWide=false;
				StringBuffer Code=new StringBuffer();

				// headers
				System.out.println("<Max_Stack>");
				System.out.println(ip.readShort());
				System.out.println("</Max_Stack>");
				System.out.println("<Num_Locals>");
				System.out.println(ip.readShort());
				System.out.println("</Num_Locals>");
				// bytecode - the big one
                Code.append("<Code_Length>\n");
				codeLenMax = ip.readInt();
                Code.append(codeLenMax+"\n");
                Code.append("</Code_Length>\n");
				Code.append("<Code>\n");
				for(codeLen=codeLenMax; codeLen > 0; codeLen--)
				{
					// take out opcodes here
					// need comparison here
					chunk = getByteChunkAsString(ip);
					pc = codeLenMax-codeLen;
					Code.append("<Line>");
					if (codeLenMax < 1000)
					{
						if (pc < 10)
							Code.append("00" + pc + ":  ");
						else if (pc < 100)
							Code.append("0" + pc + ":  ");
						else
							Code.append(pc + ":  ");
					}
					else if (codeLenMax < 10000)
					{
						if (pc < 10)
							Code.append("000" + pc + ":  ");
						else if (pc < 100)
							Code.append("00" + pc + ":  ");
						else if (pc < 1000)
							Code.append("0" + pc + ":  ");
						else
							Code.append(pc + ":  ");
					}
					else
					{
						if (pc < 10)
							Code.append("00000" + pc + ":  ");
						else if (pc < 100)
							Code.append("0000" + pc + ":  ");
						else if (pc < 1000)
							Code.append("000" + pc + ":  ");
						else if (pc < 10000)
							Code.append("00" + pc + ":  ");
						else if (pc < 100000)
							Code.append("0" + pc + ":  ");
						else
							Code.append(pc + ":  ");
					}

					if (chunk.equals("00"))
					{
						Code.append("nop");
					}
					else if (chunk.equals("01"))
					{
						Code.append("aconst_null");
					}
					else if  (chunk.equals("02"))
					{
						Code.append("iconst_m1");
					}
					else if  (chunk.equals("03"))
					{
						Code.append("iconst_0");
					}
					else if  (chunk.equals("04"))
					{
						Code.append("iconst_1");
					}
					else if  (chunk.equals("05"))
					{
						Code.append("iconst_2");
					}
					else if  (chunk.equals("06"))
					{
						Code.append("iconst_3");
					}
					else if  (chunk.equals("07"))
					{
						Code.append("iconst_4");
					}
					else if  (chunk.equals("08"))
					{
						Code.append("iconst_5");
					}
					else if  (chunk.equals("09"))
					{
						Code.append("lconst_0");
					}
					else if  (chunk.equals("0a"))
					{
						Code.append("lconst_1");
					}
					else if  (chunk.equals("0b"))
					{
						Code.append("fconst_0");
					}
					else if  (chunk.equals("0c"))
					{
						Code.append("fconst_1");
					}
					else if  (chunk.equals("0d"))
					{
						Code.append("fconst_2");
					}
					else if  (chunk.equals("0e"))
					{
						Code.append("dconst_0");
					}
					else if  (chunk.equals("0f"))
					{
						Code.append("dconst_1");
					}
					else if  (chunk.equals("10"))
					{
						Code.append("bipush");
						Code.append(" " + getByteChunkAsNumber(ip,1,0));
						codeLen--;
					}
					else if  (chunk.equals("11"))
					{
						Code.append("sipush");
						Code.append(" " + getByteChunkAsNumber(ip,2,0));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("12"))
					{
						Code.append("ldc");
						Code.append(" " + getByteChunkAsNumber(ip,1,0));
						codeLen--;
					}
					else if  (chunk.equals("13"))
					{
						Code.append("ldc_w");
						Code.append(" " + getByteChunkAsNumber(ip,2,0));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("14"))
					{
						Code.append("ldc2_w");
						Code.append(" " + getByteChunkAsNumber(ip,2,0));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("15"))
					{
						Code.append("iload");
						if (useWide)
						{
							Code.append(" " + getByteChunkAsNumber(ip,2,0));
							useWide = false;
							codeLen--;
						}
						else
							Code.append(" " + getByteChunkAsNumber(ip,1,0));
						codeLen--;
					}
					else if  (chunk.equals("16"))
					{
						Code.append("lload");
						if (useWide)
						{
							Code.append(" " + getByteChunkAsNumber(ip,2,0));
							useWide = false;
							codeLen--;
						}
						else
							Code.append(" " + getByteChunkAsNumber(ip,1,0));
						codeLen--;
					}
					else if  (chunk.equals("17"))
					{
						Code.append("fload");
						if (useWide)
						{
							Code.append(" " + getByteChunkAsNumber(ip,2,0));
							useWide = false;
							codeLen--;
						}
						else
							Code.append(" " + getByteChunkAsNumber(ip,1,0));
						codeLen--;
					}
					else if  (chunk.equals("18"))
					{
						Code.append("dload");
						if (useWide)
						{
							Code.append(" " + getByteChunkAsNumber(ip,2,0));
							useWide = false;
							codeLen--;
						}
						else
							Code.append(" " + getByteChunkAsNumber(ip,1,0));
						codeLen--;
					}
					else if  (chunk.equals("19"))
					{
						Code.append("aload");
						if (useWide)
						{
							Code.append(" " + getByteChunkAsNumber(ip,2,0));
							useWide = false;
							codeLen--;
						}
						else
							Code.append(" " + getByteChunkAsNumber(ip,1,0));
						codeLen--;
					}
					else if  (chunk.equals("1a"))
					{
						Code.append("iload_0");
					}
					else if  (chunk.equals("1b"))
					{
						Code.append("iload_1");
					}
					else if  (chunk.equals("1c"))
					{
						Code.append("iload_2");
					}
					else if  (chunk.equals("1d"))
					{
						Code.append("iload_3");
					}
					else if  (chunk.equals("1e"))
					{
						Code.append("lload_0");
					}
					else if  (chunk.equals("1f"))
					{
						Code.append("lload_1");
					}
					else if  (chunk.equals("20"))
					{
						Code.append("lload_2");
					}
					else if  (chunk.equals("21"))
					{
						Code.append("lload_3");
					}
					else if  (chunk.equals("22"))
					{
						Code.append("fload_0");
					}
					else if  (chunk.equals("23"))
					{
						Code.append("fload_1");
					}
					else if  (chunk.equals("24"))
					{
						Code.append("fload_2");
					}
					else if  (chunk.equals("25"))
					{
						Code.append("fload_3");
					}
					else if  (chunk.equals("26"))
					{
						Code.append("dload_0");
					}
					else if  (chunk.equals("27"))
					{
						Code.append("dload_1");
					}
					else if  (chunk.equals("28"))
					{
						Code.append("dload_2");
					}
					else if  (chunk.equals("29"))
					{
						Code.append("dload_3");
					}
					else if  (chunk.equals("2a"))
					{
						Code.append("aload_0");
					}
					else if  (chunk.equals("2b"))
					{
						Code.append("aload_1");
					}
					else if  (chunk.equals("2c"))
					{
						Code.append("aload_2");
					}
					else if  (chunk.equals("2d"))
					{
						Code.append("aload_3");
					}
					else if  (chunk.equals("2e"))
					{
						Code.append("iaload");
					}
					else if  (chunk.equals("2f"))
					{
						Code.append("laload");
					}
					else if  (chunk.equals("30"))
					{
						Code.append("faload");
					}
					else if  (chunk.equals("31"))
					{
						Code.append("daload");
					}
					else if  (chunk.equals("32"))
					{
						Code.append("aaload");
					}
					else if  (chunk.equals("33"))
					{
						Code.append("baload");
					}
					else if  (chunk.equals("34"))
					{
						Code.append("caload");
					}
					else if  (chunk.equals("35"))
					{
						Code.append("saload");
					}
					else if  (chunk.equals("36"))
					{
						Code.append("istore");
						if (useWide)
						{
							Code.append(" " + getByteChunkAsNumber(ip,2,0));
							useWide = false;
							codeLen--;
						}
						else
							Code.append(" " + getByteChunkAsNumber(ip,1,0));
						codeLen--;
					}
					else if  (chunk.equals("37"))
					{
						Code.append("lstore");
						if (useWide)
						{
							Code.append(" " + getByteChunkAsNumber(ip,2,0));
							useWide = false;
							codeLen--;
						}
						else
							Code.append(" " + getByteChunkAsNumber(ip,1,0));
						codeLen--;
					}
					else if  (chunk.equals("38"))
					{
						Code.append("fstore");
						if (useWide)
						{
							Code.append(" " + getByteChunkAsNumber(ip,2,0));
							useWide = false;
							codeLen--;
						}
						else
							Code.append(" " + getByteChunkAsNumber(ip,1,0));
						codeLen--;
					}
					else if  (chunk.equals("39"))
					{
						Code.append("dstore");
						if (useWide)
						{
							Code.append(" " + getByteChunkAsNumber(ip,2,0));
							useWide = false;
							codeLen--;
						}
						else
							Code.append(" " + getByteChunkAsNumber(ip,1,0));
						codeLen--;
					}
					else if  (chunk.equals("3a"))
					{
						Code.append("astore");
						if (useWide)
						{
							Code.append(" " + getByteChunkAsNumber(ip,2,0));
							useWide = false;
							codeLen--;
						}
						else
							Code.append(" " + getByteChunkAsNumber(ip,1,0));
						codeLen--;
					}
					else if  (chunk.equals("3b"))
					{
						Code.append("istore_0");
					}
					else if  (chunk.equals("3c"))
					{
						Code.append("istore_1");
					}
					else if  (chunk.equals("3d"))
					{
						Code.append("istore_2");
					}
					else if  (chunk.equals("3e"))
					{
						Code.append("istore_3");
					}
					else if  (chunk.equals("3f"))
					{
						Code.append("lstore_0");
					}
					else if  (chunk.equals("40"))
					{
						Code.append("lstore_1");
					}
					else if  (chunk.equals("41"))
					{
						Code.append("lstore_2");
					}
					else if  (chunk.equals("42"))
					{
						Code.append("lstore_3");
					}
					else if  (chunk.equals("43"))
					{
						Code.append("fstore_0");
					}
					else if  (chunk.equals("44"))
					{
						Code.append("fstore_1");
					}
					else if  (chunk.equals("45"))
					{
						Code.append("fstore_2");
					}
					else if  (chunk.equals("46"))
					{
						Code.append("fstore_3");
					}
					else if  (chunk.equals("47"))
					{
						Code.append("dstore_0");
					}
					else if  (chunk.equals("48"))
					{
						Code.append("dstore_1");
					}
					else if  (chunk.equals("49"))
					{
						Code.append("dstore_2");
					}
					else if  (chunk.equals("4a"))
					{
						Code.append("dstore_3");
					}
					else if  (chunk.equals("4b"))
					{
						Code.append("astore_0");
					}
					else if  (chunk.equals("4c"))
					{
						Code.append("astore_1");
					}
					else if  (chunk.equals("4d"))
					{
						Code.append("astore_2");
					}
					else if  (chunk.equals("4e"))
					{
						Code.append("astore_3");
					}
					else if  (chunk.equals("4f"))
					{
						Code.append("iastore");
					}
					else if  (chunk.equals("50"))
					{
						Code.append("lastore");
					}
					else if  (chunk.equals("51"))
					{
						Code.append("fastore");
					}
					else if  (chunk.equals("52"))
					{
						Code.append("dastore");
					}
					else if  (chunk.equals("53"))
					{
						Code.append("aastore");
					}
					else if  (chunk.equals("54"))
					{
						Code.append("bastore");
					}
					else if  (chunk.equals("55"))
					{
						Code.append("castore");
					}
					else if  (chunk.equals("56"))
					{
						Code.append("sastore");
					}
					else if  (chunk.equals("57"))
					{
						Code.append("pop");
					}
					else if  (chunk.equals("58"))
					{
						Code.append("pop2");
					}
					else if  (chunk.equals("59"))
					{
						Code.append("dup");
					}
					else if  (chunk.equals("5a"))
					{
						Code.append("dup_x1");
					}
					else if  (chunk.equals("5b"))
					{
						Code.append("dup_x2");
					}
					else if  (chunk.equals("5c"))
					{
						Code.append("dup2");
					}
					else if  (chunk.equals("5d"))
					{
						Code.append("dup2_x1");
					}
					else if  (chunk.equals("5e"))
					{
						Code.append("dup2_x2");
					}
					else if  (chunk.equals("5f"))
					{
						Code.append("swap");
					}
					else if  (chunk.equals("60"))
					{
						Code.append("iadd");
					}
					else if  (chunk.equals("61"))
					{
						Code.append("ladd");
					}
					else if  (chunk.equals("62"))
					{
						Code.append("fadd");
					}
					else if  (chunk.equals("63"))
					{
						Code.append("dadd");
					}
					else if  (chunk.equals("64"))
					{
						Code.append("isub");
					}
					else if  (chunk.equals("65"))
					{
						Code.append("lsub");
					}
					else if  (chunk.equals("66"))
					{
						Code.append("fsub");
					}
					else if  (chunk.equals("67"))
					{
						Code.append("dsub");
					}
					else if  (chunk.equals("68"))
					{
						Code.append("imul");
					}
					else if  (chunk.equals("69"))
					{
						Code.append("lmul");
					}
					else if  (chunk.equals("6a"))
					{
						Code.append("fmul");
					}
					else if  (chunk.equals("6b"))
					{
						Code.append("dmul");
					}
					else if  (chunk.equals("6c"))
					{
						Code.append("idiv");
					}
					else if  (chunk.equals("6d"))
					{
						Code.append("ldiv");
					}
					else if  (chunk.equals("6e"))
					{
						Code.append("fdiv");
					}
					else if  (chunk.equals("6f"))
					{
						Code.append("ddiv");
					}
					else if  (chunk.equals("70"))
					{
						Code.append("irem");
					}
					else if  (chunk.equals("71"))
					{
						Code.append("lrem");
					}
					else if  (chunk.equals("72"))
					{
						Code.append("frem");
					}
					else if  (chunk.equals("73"))
					{
						Code.append("drem");
					}
					else if  (chunk.equals("74"))
					{
						Code.append("ineg");
					}
					else if  (chunk.equals("75"))
					{
						Code.append("lneg");
					}
					else if  (chunk.equals("76"))
					{
						Code.append("fneg");
					}
					else if  (chunk.equals("77"))
					{
						Code.append("dneg");
					}
					else if  (chunk.equals("78"))
					{
						Code.append("ishl");
					}
					else if  (chunk.equals("79"))
					{
						Code.append("lshl");
					}
					else if  (chunk.equals("7a"))
					{
						Code.append("ishr");
					}
					else if  (chunk.equals("7b"))
					{
						Code.append("lshr");
					}
					else if  (chunk.equals("7c"))
					{
						Code.append("iushr");
					}
					else if  (chunk.equals("7d"))
					{
						Code.append("lushr");
					}
					else if  (chunk.equals("7e"))
					{
						Code.append("iand");
					}
					else if  (chunk.equals("7f"))
					{
						Code.append("land");
					}
					else if  (chunk.equals("80"))
					{
						Code.append("ior");
					}
					else if  (chunk.equals("81"))
					{
						Code.append("lor");
					}
					else if  (chunk.equals("82"))
					{
						Code.append("ixor");
					}
					else if  (chunk.equals("83"))
					{
						Code.append("lxor");
					}
					else if  (chunk.equals("84"))
					{
						Code.append("iinc");
						if (useWide)
							Code.append(" " + getByteChunkAsNumber(ip,2,0));
						else
							Code.append(" " + getByteChunkAsNumber(ip,1,0));
						// second part needs to be signed
						if (useWide)
							Code.append(" " + ip.readShort());
						else
							Code.append(" " + ip.readByte());
						// Code.append(" " + getByteChunkAsNumber(ip,1,0));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("85"))
					{
						Code.append("i2l");
					}
					else if  (chunk.equals("86"))
					{
						Code.append("i2f");
					}
					else if  (chunk.equals("87"))
					{
						Code.append("i2d");
					}
					else if  (chunk.equals("88"))
					{
						Code.append("l2i");
					}
					else if  (chunk.equals("89"))
					{
						Code.append("l2f");
					}
					else if  (chunk.equals("8a"))
					{
						Code.append("l2d");
					}
					else if  (chunk.equals("8b"))
					{
						Code.append("f2i");
					}
					else if  (chunk.equals("8c"))
					{
						Code.append("f2l");
					}
					else if  (chunk.equals("8d"))
					{
						Code.append("f2d");
					}
					else if  (chunk.equals("8e"))
					{
						Code.append("d2i");
					}
					else if  (chunk.equals("8f"))
					{
						Code.append("d2l");
					}
					else if  (chunk.equals("90"))
					{
						Code.append("d2f");
					}
					else if  (chunk.equals("91"))
					{
						Code.append("i2b");
					}
					else if  (chunk.equals("92"))
					{
						Code.append("i2c");
					}
					else if  (chunk.equals("93"))
					{
						Code.append("i2s");
					}
					else if  (chunk.equals("94"))
					{
						Code.append("lcmp");
					}
					else if  (chunk.equals("95"))
					{
						Code.append("fcmpl");
					}
					else if  (chunk.equals("96"))
					{
						Code.append("fcmpg");
					}
					else if  (chunk.equals("97"))
					{
						Code.append("dcmpl");
					}
					else if  (chunk.equals("98"))
					{
						Code.append("dcmpg");
					}
					else if  (chunk.equals("99"))
					{
						Code.append("ifeq");
						Code.append(" " + ((ip.readByte() << 8 | ip.readByte())+codeLenMax-codeLen));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("9a"))
					{
						Code.append("ifne");
						Code.append(" " + ((ip.readByte() << 8 | ip.readByte())+codeLenMax-codeLen));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("9b"))
					{
						Code.append("iflt");
						Code.append(" " + ((ip.readByte() << 8 | ip.readByte())+codeLenMax-codeLen));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("9c"))
					{
						Code.append("ifge");
						Code.append(" " + ((ip.readByte() << 8 | ip.readByte())+codeLenMax-codeLen));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("9d"))
					{
						Code.append("ifgt");
						Code.append(" " + ((ip.readByte() << 8 | ip.readByte())+codeLenMax-codeLen));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("9e"))
					{
						Code.append("ifle");
						Code.append(" " + ((ip.readByte() << 8 | ip.readByte())+codeLenMax-codeLen));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("9f"))
					{
						Code.append("if_icmpeq");
						Code.append(" " + ((ip.readByte() << 8 | ip.readByte())+codeLenMax-codeLen));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("a0"))
					{
						Code.append("if_icmpne");
						Code.append(" " + ((ip.readByte() << 8 | ip.readByte())+codeLenMax-codeLen));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("a1"))
					{
						Code.append("if_icmplt");
						Code.append(" " + ((ip.readByte() << 8 | ip.readByte())+codeLenMax-codeLen));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("a2"))
					{
						Code.append("if_icmpge");
						Code.append(" " + ((ip.readByte() << 8 | ip.readByte())+codeLenMax-codeLen));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("a3"))
					{
						Code.append("if_icmpgt");
						Code.append(" " + ((ip.readByte() << 8 | ip.readByte())+codeLenMax-codeLen));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("a4"))
					{
						Code.append("if_icmple");
						Code.append(" " + ((ip.readByte() << 8 | ip.readByte())+codeLenMax-codeLen));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("a5"))
					{
						Code.append("if_acmpeq");
						Code.append(" " + ((ip.readByte() << 8 | ip.readByte())+codeLenMax-codeLen));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("a6"))
					{
						Code.append("if_acmpne");
						Code.append(" " + ((ip.readByte() << 8 | ip.readByte())+codeLenMax-codeLen));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("a7"))
					{
						Code.append("goto");
						int sign=ip.readByte(), number=Integer.parseInt(getByteChunkAsNumber(ip,1,0));
//System.out.println(ip.getFilePointer() + ": " + (sign<<8) + ", " + number + ", " + ((sign << 8 | number)+codeLenMax-codeLen));
						Code.append(" " + ((sign << 8 | number)+codeLenMax-codeLen));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("a8"))
					{
						Code.append("jsr");
						Code.append(" " + getByteChunkAsNumber(ip,2,(codeLenMax-codeLen)));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("a9"))
					{
						Code.append("ret");
						if (useWide)
						{
							Code.append(" " + getByteChunkAsNumber(ip,2,(codeLenMax-codeLen)));
							useWide = false;
							codeLen--;
						}
						else
							Code.append(" " + getByteChunkAsNumber(ip,1,(codeLenMax-codeLen)));
						codeLen--;
					}
					else if  (chunk.equals("aa"))
					{
						Code.append("tableswitch" + "\n");
						// start off with just jumping the right number of bytecodes
						int j;
						int padding = (4-((pc+1)%4))%4;
//System.out.println("//pc:" + pc + "; Padding: " + padding);
						if (padding!=0)
							getByteChunkAsNumber(ip,padding,0);
//System.out.println("//FilePos:" + ip.getFilePointer() + "; Padding: " + padding + "\n");
						int defaultoffset = ip.readInt() + pc;
//System.out.println("//Default:" + defaultoffset);
						int lownum = ip.readInt(), highnum = ip.readInt();
//System.out.println("	" + lownum + " : " + highnum);
						Code.append("  " + lownum + " : " + highnum);

						for (j = lownum; j<=highnum; j++)
						{
//System.out.println("j: " + j + "; highnum: " + highnum);
							Code.append("  " + j + ": " + (ip.readInt()+pc));
					    }
						Code.append("	default: " + defaultoffset);
						codeLen-= padding + 12 + 4*(highnum-lownum+1);
					}
					else if  (chunk.equals("ab"))
					{
						Code.append("lookupswitch  ");
						// start off with just jumping the right number of bytecodes
						int j;
						int padding = (4-((pc+1)%4))%4;
//System.out.println("//FilePos:" + ip.getFilePointer() + "; Padding: " + padding + "\n");
						if (padding!=0)
							getByteChunkAsNumber(ip,padding,0);
						String defaultoffset = "" + ip.readInt();
						j = Integer.parseInt(defaultoffset) + pc;
						defaultoffset = (" " + j);
						int num = ip.readInt();
						Code.append("  " + num);

						for (j = 0; j<num; j++)
							Code.append("  " + ip.readInt() + ": " + (ip.readInt()+pc) + "\n");
						Code.append("  default:" + defaultoffset);
						codeLen-= padding + 8 + 8*num;
					}
					else if  (chunk.equals("ac"))
					{
						Code.append("ireturn");
					}
					else if  (chunk.equals("ad"))
					{
						Code.append("lreturn");
					}
					else if  (chunk.equals("ae"))
					{
						Code.append("freturn");
					}
					else if  (chunk.equals("af"))
					{
						Code.append("dreturn");
					}
					else if  (chunk.equals("b0"))
					{
						Code.append("areturn");
					}
					else if  (chunk.equals("b1"))
					{
						Code.append("return");
					}
					else if  (chunk.equals("b2"))
					{
						Code.append("getstatic");
						Code.append(" " + getByteChunkAsNumber(ip,2,0));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("b3"))
					{
						Code.append("putstatic");
						Code.append(" " + getByteChunkAsNumber(ip,2,0));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("b4"))
					{
						Code.append("getfield");
						Code.append(" " + getByteChunkAsNumber(ip,2,0));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("b5"))
					{
						Code.append("putfield");
						Code.append(" " + getByteChunkAsNumber(ip,2,0));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("b6"))
					{
						Code.append("invokevirtual");
						Code.append(" " + getByteChunkAsNumber(ip,2,0));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("b7"))
					{
						Code.append("invokespecial");
						Code.append(" " + getByteChunkAsNumber(ip,2,0));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("b8"))
					{
						Code.append("invokestatic");
						Code.append(" " + getByteChunkAsNumber(ip,2,0));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("b9"))
					{
						Code.append("invokeinterface");
						Code.append(" " + getByteChunkAsNumber(ip,4,0));
						codeLen--;codeLen--;codeLen--;codeLen--;
					}
					else if  (chunk.equals("ba"))
					{
						Code.append("xxxunusedxxx");
					}
					else if  (chunk.equals("bb"))
					{
						Code.append("new");
						Code.append(" " + getByteChunkAsNumber(ip,2,0));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("bc"))
					{
						Code.append("newarray");
						// 4 - boolean, 5 - char, 6 - float, 7 - double
						// 8 - byte, 9 - short, 10 - int, 11 - long
						Code.append(" " + getByteChunkAsNumber(ip,1,0));
						codeLen--;
					}
					else if  (chunk.equals("bd"))
					{
						Code.append("anewarray");
						Code.append(" " + getByteChunkAsNumber(ip,2,0));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("be"))
					{
						Code.append("arraylength");
					}
					else if  (chunk.equals("bf"))
					{
						Code.append("athrow");
					}
					else if  (chunk.equals("c0"))
					{
						Code.append("checkcast");
						Code.append(" " + getByteChunkAsNumber(ip,2,0));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("c1"))
					{
						Code.append("instanceof");
						Code.append(" " + getByteChunkAsNumber(ip,2,0));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("c2"))
					{
						Code.append("monitorenter");
					}
					else if  (chunk.equals("c3"))
					{
						Code.append("monitorexit");
					}
					else if  (chunk.equals("c4"))
					{
						Code.append("wide");
						useWide=true;
						codeLen--;
					}
					else if  (chunk.equals("c5"))
					{
						Code.append("multianewarray");
						Code.append(" " + getByteChunkAsNumber(ip,2,0) + "," + getByteChunkAsNumber(ip,1,0));
						codeLen--;codeLen--;codeLen--;
					}
					else if  (chunk.equals("c6"))
					{
						Code.append("ifnull");
						Code.append(" " + ((ip.readByte() << 8 | ip.readByte())+codeLenMax-codeLen));
						// Code.append(" " + getByteChunkAsNumber(ip,2,(codeLenMax-codeLen)));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("c7"))
					{
						Code.append("ifnonnull");
						Code.append(" " + ((ip.readByte() << 8 | ip.readByte())+codeLenMax-codeLen));
						// Code.append(" " + getByteChunkAsNumber(ip,2,(codeLenMax-codeLen)));
						codeLen--;codeLen--;
					}
					else if  (chunk.equals("c8"))
					{
						Code.append("goto_w");
						Code.append(" " + getByteChunkAsNumber(ip,4,(codeLenMax-codeLen)));
						codeLen--;codeLen--;codeLen--;codeLen--;
					}
					else if  (chunk.equals("c9"))
					{
						Code.append("jsr_w");
						Code.append(" " + getByteChunkAsNumber(ip,4,(codeLenMax-codeLen)));
						codeLen--;codeLen--;codeLen--;codeLen--;
					}
					else if  (chunk.equals("ca"))
					{
						Code.append("breakpoint");
					}
					else if  (chunk.equals("fe"))
					{
						Code.append("impdep1");
					}
					else if  (chunk.equals("ff"))
					{
						Code.append("impdep2");
					}
					else
					{
						Code.append("ERROR!");
					}
					Code.append("\n");
					Code.append("</Line>\n");
				}
				Code.append("</Code>\n");



				System.out.print("\n");
				// read in the exception table
				numExcept = ip.readShort();
				//System.out.println("");
				System.out.println("<ExceptionTable_Length>" + numExcept + "</ExceptionTable_Length>");

				System.out.println("<ExceptionTables>");
				while(numExcept > 0)
				{
					System.out.println("<ExceptionTable>");
					System.out.println("<StartPC> " + ip.readShort() + "</StartPC>");
					System.out.println("<EndPC>" + ip.readShort() + "</EndPC>");
					System.out.println("<HandlerPC>" + ip.readShort() + "</HandlerPC>");
					System.out.println("<CatchType>" + ip.readShort() + " </CatchType>");
					System.out.println("</ExceptionTable>");
					numExcept--;
				}
				System.out.println("</ExceptionTables>");

				System.out.print(Code.toString());

				attCount=ip.readShort();
				System.out.println("<CodeAttribute_Count>" + attCount + "</CodeAttribute_Count>");

				// Debugging information - line num's or local variables - is optional.

				if (attCount>0)
				{
					System.out.println("<CodeAttribute_Name_Index>" + ip.readShort() + "</CodeAttribute_Name_Index>");
					System.out.println("<CodeAttribute_Length>" + ip.readInt() + "</CodeAttribute_Length>");
					numLines = ip.readShort();
					System.out.println("<LineNumTable_Count>" + numLines + "</LineNumTable_Count>");
					System.out.println("<LineNumTable>");
					for(;numLines > 0; numLines--)
					{
						System.out.println("<LineNumMapping>");
						System.out.println("<StartPC>" + ip.readShort() + "</StartPC>");
						System.out.println("<LineNum>" + ip.readShort() + "</LineNum>");
						System.out.println("</LineNumMapping>");
					}
					System.out.println("</LineNumTable>");
				}
			}
			else
			{
				attLen = ip.readInt();
				//System.out.println("\tAttribute Length: " + attLen);
				if(attLen > 0)
				{
					ip.readShort();
					//System.out.println("\tAttribute Value index: " + ip.readShort());
				}
			}

			System.out.println("</Attribute>");
		}
		catch (ArrayIndexOutOfBoundsException e)
		{
			System.err.println("Caught ArrayIndexOutOfBoundsException: " +
				e.getMessage());
		}
		catch (IOException e)
		{
			System.err.println("Caught IOException: " +
				e.getMessage());
		}
		catch (Exception e)
		{
			System.err.println("File read error:" + e.getMessage());
		}
	}



	ClassParser(RandomAccessFile in) throws IOException {

		int ClassAccess, ClassName, SuperClass;

		System.out.println("<?xml version=\"1.0\" ?> ");
		System.out.println("<root>");
		printCafeBabe(in);
		getConstantPool(in);
		System.out.println("<ConstantPool_Count>" + numEntries + "</ConstantPool_Count>");
		System.out.println("<ConstantPool>");
		//output constant pool
		for(int i = 1; i < numEntries; i++) {
			System.out.println("<Tag>");
			System.out.println("<ConstantPool_Index>" + i + "</ConstantPool_Index>");
			System.out.println("<Type>" + constantPool[i][0] + "</Type>");
			System.out.println("<Value>" + "\n" + constantPool[i][1] + "\n" + "</Value>");
			System.out.println("</Tag>");
		}
		System.out.println("</ConstantPool>");

		// access flags & this and super


		ClassAccess = in.readShort();

		/*  const unsigned short ACC_PUBLIC 		= 0x1;
			const unsigned short ACC_PRIVATE		= 0x2;
			const unsigned short ACC_PROTECTED 		= 0x4;
			const unsigned short ACC_STATIC	 		= 0x8;
			const unsigned short ACC_FINAL 			= 0x10;
			const unsigned short ACC_SYNCHRONIZED 	= 0x20;  This should always be set in modern Java files, so ignore
			const unsigned short ACC_VOLATILE 		= 0x40;
			const unsigned short ACC_TRANSIENT 		= 0x80;
			const unsigned short ACC_NATIVE 		= 0x100;
			const unsigned short ACC_INTERFACE 		= 0x200;
			const unsigned short ACC_ABSTRACT 		= 0x400;
		 */
		System.out.println("<AccessFlags>");
		System.out.print(getAccessFlags(ClassAccess));
		System.out.println("</AccessFlags>");
		// System.out.println("Access flags: Ox" + getByteChunkAsString(in) + getByteChunkAsString(in));
		ClassName = in.readShort();
		SuperClass = in.readShort();
		System.out.println("<ThisClass>");
		System.out.println(ClassName);
		System.out.println("</ThisClass>");
		System.out.println("<SuperClass>");
		System.out.println(SuperClass);
		System.out.println("</SuperClass>");


		// interfaces
		numEntries = in.readShort();
		System.out.println("<Interface_Count>" + numEntries + "</Interface_Count>");

		System.out.println("<Interfaces>");
		for(int i = 1; i <= numEntries; i++) {
			in.readShort();
		//	System.out.println("Interface " + i + " is in constant_pool[" + in.readShort() + "]");
		}
		System.out.println("</Interfaces>");


		// fields
		getFields(in);

		// methods
		getMethods(in);

		// remaining attributes
		numEntries = in.readShort();
		System.out.println("<Attribute_Count>" + numEntries + "</Attribute_Count>");

		System.out.println("<Attributes>");
		for(int i=0; i < numEntries; i++)
			getAttributes(in);
		System.out.println("</Attributes>");

		System.out.println("</root>");
		}

	}



public class ClassToXML {
   	public static void main(String[] args) {
		RandomAccessFile in = null;
		PrintStream out = null;
		ClassParser inClass;

		// quick args check
		if ((args.length < 1) || (args.length > 1) || (args[0].endsWith(".class") == false)){
	        System.out.println("Usage: java javaq File.class");
	        System.exit(0);
		}

		try {
			// read it in
			in = new RandomAccessFile(args[0], "rw");
			inClass = new ClassParser(in);
			in.close();
		}catch (IOException e) {
        	System.err.println("File cannot be read:" + e.getMessage());
    	}
	}
}
