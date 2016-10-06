package XMLToSource;
import java_cup.runtime.Symbol;

%%	
%cup
%%

"<?xml version=\""|"?>"				{ /* ignore */                                   }

"<root>"					{ return new Symbol(sym.ROOT,yytext());          }
"</root>"					{ return new Symbol(sym.XROOT,yytext());         }
"<MagicNumber>"					{ return new Symbol(sym.MAGICNUM,yytext());      }
"</MagicNumber>"				{ return new Symbol(sym.XMAGICNUM,yytext());     }
"<MajorVersion>"				{ return new Symbol(sym.MAJORVER,yytext());      }
"</MajorVersion>"				{ return new Symbol(sym.XMAJORVER,yytext());     } 
"<MinorVersion>"				{ return new Symbol(sym.MINORVER,yytext());      }
"</MinorVersion>" 				{ return new Symbol(sym.XMINORVER,yytext());     }
"<ConstantPool_Count>"				{ return new Symbol(sym.CPCOUNT,yytext());       }
"</ConstantPool_Count>"				{ return new Symbol(sym.XCPCOUNT,yytext());      }
"<ConstantPool>"				{ return new Symbol(sym.CONSTPOOL,yytext());     }
"</ConstantPool>"				{ return new Symbol(sym.XCONSTPOOL,yytext());    }
"<Tag>"						{ return new Symbol(sym.CPTAG,yytext());         }
"</Tag>"					{ return new Symbol(sym.XCPTAG,yytext());        }
"<ConstantPool_Index>"				{ return new Symbol(sym.CPINDEX,yytext());       }
"</ConstantPool_Index>"				{ return new Symbol(sym.XCPINDEX,yytext());      }
"<Type>"					{ return new Symbol(sym.TYPETAG,yytext());       }
"</Type>"					{ return new Symbol(sym.XTYPETAG,yytext());      }
"<AccessFlags>"					{ return new Symbol(sym.ACCFLAGS,yytext());      }
"</AccessFlags>"				{ return new Symbol(sym.XACCFLAGS,yytext());     }
"<Name_Index>"					{ return new Symbol(sym.NAMEINDEX,yytext());     }
"</Name_Index>"					{ return new Symbol(sym.XNAMEINDEX,yytext());    }
"<Description_Index>"				{ return new Symbol(sym.DESCINDEX,yytext());     }
"</Description_Index>"				{ return new Symbol(sym.XDESCINDEX,yytext());    }
"<Value>"					{ return new Symbol(sym.VALTAG,yytext());        }
"</Value>" 					{ return new Symbol(sym.XVALTAG,yytext());       }
"<ThisClass>"					{ return new Symbol(sym.THISCL,yytext());        }
"</ThisClass>"					{ return new Symbol(sym.XTHISCL,yytext());       }
"<SuperClass>"					{ return new Symbol(sym.SUPERCL,yytext());       }
"</SuperClass>"					{ return new Symbol(sym.XSUPERCL,yytext());      }

"<Interface_Count>"				{ return new Symbol(sym.INTCNT,yytext());        }
"</Interface_Count>"				{ return new Symbol(sym.XINTCNT,yytext());       }
"<Interfaces>"					{ return new Symbol(sym.INTERFACES,yytext());    }
"</Interfaces>"					{ return new Symbol(sym.XINTERFACES,yytext());   }
"<Field_Count>"					{ return new Symbol(sym.FIELDCNT,yytext());      }
"</Field_Count>"				{ return new Symbol(sym.XFIELDCNT,yytext());     }
"<Fields>"					{ return new Symbol(sym.FIELDS,yytext());        }
"</Fields>"					{ return new Symbol(sym.XFIELDS,yytext());       }
"<Field>"					{ return new Symbol(sym.FIELD,yytext());         }
"</Field>"					{ return new Symbol(sym.XFIELD,yytext());        }
"<Method_Count>"				{ return new Symbol(sym.METHCNT,yytext());       }
"</Method_Count>"				{ return new Symbol(sym.XMETHCNT,yytext());      }
"<Methods>"					{ return new Symbol(sym.METHODS,yytext());       }
"</Methods>"					{ return new Symbol(sym.XMETHODS,yytext());      }
"<Method>"					{ return new Symbol(sym.METHOD,yytext());        }
"</Method>"					{ return new Symbol(sym.XMETHOD,yytext());       }
"<Attribute_Count>"				{ return new Symbol(sym.ATTCNT,yytext());        }
"</Attribute_Count>"				{ return new Symbol(sym.XATTCNT,yytext());       }
"<Attributes>"					{ return new Symbol(sym.ATTRIBS,yytext());       }
"</Attributes>"					{ return new Symbol(sym.XATTRIBS,yytext());      }
"<Attribute>"					{ return new Symbol(sym.ATTRIB,yytext());        }
"</Attribute>"					{ return new Symbol(sym.XATTRIB,yytext());       }
"<Attribute_Type>"				{ return new Symbol(sym.ATTTYPE,yytext());       }
"</Attribute_Type>"				{ return new Symbol(sym.XATTTYPE,yytext());      }
"<Attribute_Type_Index>"			{ return new Symbol(sym.ATTTYPEINDEX,yytext());  }
"</Attribute_Type_Index>"			{ return new Symbol(sym.XATTTYPEINDEX,yytext()); }
"<Attribute_Length>"				{ return new Symbol(sym.ATTLENGTH,yytext());     }
"</Attribute_Length>"				{ return new Symbol(sym.XATTLENGTH,yytext());    }
"<Max_Stack>"					{ return new Symbol(sym.MAXSTACK,yytext());      }
"</Max_Stack>"					{ return new Symbol(sym.XMAXSTACK,yytext());     }
"<Num_Locals>"					{ return new Symbol(sym.MINSTACK,yytext());      }
"</Num_Locals>"					{ return new Symbol(sym.XMINSTACK,yytext());     }
"<Code_Length>"					{ return new Symbol(sym.CODELEN,yytext());       }
"</Code_Length>"				{ return new Symbol(sym.XCODELEN,yytext());      }
"<Code>"					{ return new Symbol(sym.CODETAG,yytext());       }
"</Code>"					{ return new Symbol(sym.XCODETAG,yytext());      }
"<Line>"					{ return new Symbol(sym.LINETAG,yytext());       }
"</Line>"					{ return new Symbol(sym.XLINETAG,yytext());      }
"<ExceptionTable_Length>"			{ return new Symbol(sym.EXCLEN,yytext());        }
"</ExceptionTable_Length>"			{ return new Symbol(sym.XEXCLEN,yytext());       }
"<ExceptionTables>"				{ return new Symbol(sym.EXCTABLES,yytext());     }
"</ExceptionTables>"				{ return new Symbol(sym.XEXCTABLES,yytext());    }
"<ExceptionTable>"				{ return new Symbol(sym.EXCTABLE,yytext());      }
"</ExceptionTable>"				{ return new Symbol(sym.XEXCTABLE,yytext());     }
"<CodeAttribute_Count>"				{ return new Symbol(sym.CODEATTCNT,yytext());    }
"</CodeAttribute_Count>"			{ return new Symbol(sym.XCODEATTCNT,yytext());   }
"<CodeAttribute_Name_Index>"			{ return new Symbol(sym.CODEATTNAME,yytext());   }
"</CodeAttribute_Name_Index>"			{ return new Symbol(sym.XCODEATTNAME,yytext());  }
"<CodeAttribute_Length>"			{ return new Symbol(sym.CODEATTLEN,yytext());    }
"</CodeAttribute_Length>"			{ return new Symbol(sym.XCODEATTLEN,yytext());   }
"<LineNumTable_Count>"				{ return new Symbol(sym.LNTABLECNT,yytext());    }
"</LineNumTable_Count>"				{ return new Symbol(sym.XLNTABLECNT,yytext());   }
"<LineNumTable>"				{ return new Symbol(sym.LINENUMTABLE,yytext());  }
"</LineNumTable>"				{ return new Symbol(sym.XLINENUMTABLE,yytext()); }
"<LineNum>"					{ return new Symbol(sym.LINENUM,yytext());       }
"</LineNum>" 					{ return new Symbol(sym.XLINENUM,yytext());      }
"<LineNumMapping>"				{ return new Symbol(sym.LNMAP,yytext());         }
"</LineNumMapping>"				{ return new Symbol(sym.XLNMAP,yytext());        }
"<StartPC>"					{ return new Symbol(sym.STARTPC,yytext());       }
"</StartPC>"					{ return new Symbol(sym.XSTARTPC,yytext());      }
"<EndPC>"					{ return new Symbol(sym.ENDPC,yytext());         }
"</EndPC>"					{ return new Symbol(sym.XENDPC,yytext());        }
"<HandlerPC>"					{ return new Symbol(sym.HANDLER,yytext());       }
"</HandlerPC>"					{ return new Symbol(sym.XHANDLER,yytext());      }
"<CatchType>"					{ return new Symbol(sym.CATCHTYPE,yytext());     }
"</CatchType>"					{ return new Symbol(sym.XCATCHTYPE,yytext());    }
"<ExceptionsNum>" 				{ return new Symbol(sym.EXCNUM,yytext());        }
"</ExceptionsNum>"				{ return new Symbol(sym.XEXCNUM,yytext());       }
"<Exceptions>"					{ return new Symbol(sym.EXCEPTIONS,yytext());    }
"</Exceptions>"					{ return new Symbol(sym.XEXCEPTIONS,yytext());   }
"<Exception>"					{ return new Symbol(sym.EXCEPTION,yytext());     }
"</Exception>"					{ return new Symbol(sym.XEXCEPTION,yytext());    }
"<InnerClassesNum>" 				{ return new Symbol(sym.ICLASSNUM,yytext());     }
"</InnerClassesNum>"				{ return new Symbol(sym.XICLASSNUM,yytext());    }
"<InnerClasses>"				{ return new Symbol(sym.ICLASSES,yytext());      }
"</InnerClasses>"				{ return new Symbol(sym.XICLASSES,yytext());     }
"<InnerClass>"					{ return new Symbol(sym.ICLASS,yytext());        }
"</InnerClass>"					{ return new Symbol(sym.XICLASS,yytext());       }
"<InnerClassIndex>"				{ return new Symbol(sym.ICLASSIDX,yytext());     }
"</InnerClassIndex>"				{ return new Symbol(sym.XICLASSIDX,yytext());    }
"<OuterClassIndex>"				{ return new Symbol(sym.OCLASSIDX,yytext());     }
"</OuterClassIndex>"				{ return new Symbol(sym.XOCLASSIDX,yytext());    }
"<InnerClassName>"				{ return new Symbol(sym.ICLASSNAME,yytext());    }
"</InnerClassName>"				{ return new Symbol(sym.XICLASSNAME,yytext());   }
"<Source_File_Index>"				{ return new Symbol(sym.SRCFILE,yytext());       }
"</Source_File_Index>"				{ return new Symbol(sym.XSRCFILE,yytext());      }
"<Constant_Value_Index>"			{ return new Symbol(sym.CONSTIDX,yytext());      }
"</Constant_Value_Index>"			{ return new Symbol(sym.XCONSTIDX,yytext());     }

"SourceFile"|"ConstantValue"|"Code"|"Exceptions"|"InnerClasses"|"Synthetic"|"LineNumberTable"|"LocalVariableTable"|"Deprecated"
						{ return new Symbol(sym.ATTRIBNAME,yytext());    }
"public"|"private"|"protected"			{ return new Symbol(sym.ACCESS,yytext());        }
"static"|"final"|"volatile"|"interface"|"abstract" 
						{ return new Symbol(sym.PROPERTY,yytext());      }
"CONSTVAL"+[_A-Za-z0-9!?<>/'`~\\$&\[\]=().,;"\\ "]+|"CONSTVAL"
						{ return new Symbol(sym.CONSTNAME,yytext());     }
"CONSTANT_"					{ return new Symbol(sym.CONSTANT,yytext());      }

"Utf8"						{ return new Symbol(sym.CHARRAY,yytext());       }
"Integer"					{ return new Symbol(sym.INTEGER,yytext());       }
"Float"						{ return new Symbol(sym.FLOAT,yytext());         }
"Long"						{ return new Symbol(sym.LONG,yytext());          }
"Double"					{ return new Symbol(sym.DOUBLE,yytext());        }
"String"					{ return new Symbol(sym.STRING,yytext());        }
"Class"						{ return new Symbol(sym.CLASSREF,yytext());      }
"Fieldref"					{ return new Symbol(sym.FIELDREF,yytext());      }
"Methodref"					{ return new Symbol(sym.METHODREF,yytext());     }
"InterfaceMethodref"				{ return new Symbol(sym.INTERFACEREF,yytext());  }
"NameAndType"					{ return new Symbol(sym.NAMEANDTYPE,yytext());   }
"default"					{ return new Symbol(sym.DEFAULT,yytext());   }

"."						{ return new Symbol(sym.DECIMALPT,yytext());     }
","						{ return new Symbol(sym.COMMA,yytext());         }
"-"						{ return new Symbol(sym.NEGATIVE,yytext());      }
[0-9]+    					{ return new Symbol(sym.NUMBER, new Integer(yytext())); }
"0x"+[0-9a-f]+					{ return new Symbol(sym.HEXNUM,yytext());        }
":"						{ /* ignore */                                   }
[ \t\r\n]+   					{ /* ignore white space */                       }
"nop"						{ return new Symbol(sym.NOP,yytext());	         } 
"m1"		 				{ return new Symbol(sym.M1,yytext());            } 
"cmp"+[lg]					{ return new Symbol(sym.CMP,yytext());           } 
[bcifld]+"2"					{ return new Symbol(sym.T2T,yytext()); 
				 	         /* This is actually [bcifld]+"2"+[bcifld] */    } 
"bipush"|"sipush"				{ return new Symbol(sym.IPUSH,yytext());         } 
"ldc"+("2"|"2_w")?				{ return new Symbol(sym.LDC,yytext());           } 
[abcsilfd]					{ return new Symbol(sym.TYPE,yytext());          } 
"null"						{ return new Symbol(sym.NULL,yytext());          } 
"const"+"_"? 					{ return new Symbol(sym.CONST,yytext());         } 
[bciflda]+"aload"				{ return new Symbol(sym.ALOAD,yytext());         }
"load"+"_"?					{ return new Symbol(sym.LOAD,yytext());          } 
[bciflda]+"astore"				{ return new Symbol(sym.ASTORE,yytext());        }
"store"+"_"?					{ return new Symbol(sym.STORE,yytext());         }
"pop"+("2")?					{ return new Symbol(sym.POP,yytext());           } 
"dup"+("2")?					{ return new Symbol(sym.DUP,yytext());           } 
"dup"+("_x1"|"_x2"|"2_x1"|"2_x2")		{ return new Symbol(sym.DUPX,yytext());          } 
"swap"						{ return new Symbol(sym.SWAP,yytext());          } 
"neg"						{ return new Symbol(sym.NEG,yytext());           } 
"add"						{ return new Symbol(sym.ADD,yytext());           } 
"sub"						{ return new Symbol(sym.SUB,yytext());           } 
"mul"						{ return new Symbol(sym.MUL,yytext());           } 
"div"						{ return new Symbol(sym.DIV,yytext());           } 
"rem"						{ return new Symbol(sym.REM,yytext());           } 
"ushr"|"shr"					{ return new Symbol(sym.SHR,yytext());           } 
"ushl"|"shl"					{ return new Symbol(sym.SHL,yytext());           } 
"and"						{ return new Symbol(sym.AND,yytext());           } 
"or"						{ return new Symbol(sym.OR,yytext());            } 
"xor"						{ return new Symbol(sym.XOR,yytext());           } 
"iinc"						{ return new Symbol(sym.IINC,yytext());          } 
"if_icmp"+("eq"|"lt"|"le"|"ne"|"gt"|"ge") 	{ return new Symbol(sym.IF_ICMP,yytext());       } 
"if"+("eq"|"lt"|"le"|"ne"|"gt"|"ge"|"null"|"nonnull")
						{ return new Symbol(sym.IF,yytext());            } 
"goto"+("_w")?					{ return new Symbol(sym.GOTO,yytext());          } 
"jsr"+("_w")?					{ return new Symbol(sym.JSR,yytext());           } 
"ret"						{ return new Symbol(sym.RET,yytext());           } 
"tableswitch"					{ return new Symbol(sym.TABLESWITCH,yytext());   } 
"lookupswitch"					{ return new Symbol(sym.LOOKUPSWITCH,yytext());  } 
"return"					{ return new Symbol(sym.RETURN,yytext());        } 
"getstatic"					{ return new Symbol(sym.GETSTATIC,yytext());     } 
"getfield"					{ return new Symbol(sym.GETFIELD,yytext());      } 
"putstatic"					{ return new Symbol(sym.PUTSTATIC,yytext());     } 
"putfield"					{ return new Symbol(sym.PUTFIELD,yytext());      } 
"invoke"+("special"|"virtual"|"static")		{ return new Symbol(sym.INVOKE,yytext());        } 
"new"						{ return new Symbol(sym.NEW,yytext());           } 
"newarray"					{ return new Symbol(sym.NEWARRAY,yytext());      } 
"arraylength"					{ return new Symbol(sym.ARRAYLENGTH,yytext());   } 
"athrow"					{ return new Symbol(sym.ATHROW,yytext());        } 
"checkcast"					{ return new Symbol(sym.CHECKCAST,yytext());     } 
"instanceof"					{ return new Symbol(sym.INSTANCEOF,yytext());    } 
"monitorenter"					{ return new Symbol(sym.MONITORENTER,yytext());  } 
"monitorexit"					{ return new Symbol(sym.MONITOREXIT,yytext());   } 
"wide"						{ return new Symbol(sym.WIDE,yytext());          } 
"multianewarray"				{ return new Symbol(sym.MULTIANEWARRAY,yytext());} 
. 						{ /*System.out.print(yytext());*/                }