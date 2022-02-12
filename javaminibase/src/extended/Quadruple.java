/* File Quadruple.java */

package extended;

import java.io.*;
import heap.*;
import global.*;


public class Quadruple implements GlobalConst{


 /** 
  * Maximum size of any quadruple
  */
  public static final int max_size = MINIBASE_PAGESIZE;

 /** 
   * a byte array to hold data
   */
  private byte [] data;

  /**
   * start position of this quadruple in data[]
   */
  private int quadruple_offset;

  /**
   * length of this quadruple
   */
  private int quadruple_length;

  /** 
   * private field
   * Number of fields in this quadruple
   */
  private short fldCnt;

  /** 
   * private field
   * Array of offsets of the fields
   */
 
  private short [] fldOffset; 

   /**
    * Class constructor
    * Creat a new quadruple with length = max_size,quadruple offset = 0.
    */

  public  Quadruple()
  {
       // Creat a new quadruple
       data = new byte[max_size];
       quadruple_offset = 0;
       quadruple_length = max_size;
  }
   
   /** Constructor
    * @param aquadruple a byte array which contains the quadruple
    * @param offset the offset of the quadruple in the byte array
    * @param length the length of the quadruple
    */

   public Quadruple(byte [] aquadruple, int offset, int length)
   {
      data = aquadruple;
      quadruple_offset = offset;
      quadruple_length = length;
    //  fldCnt = getShortValue(offset, data);
   }
   
   /** Constructor(used as quadruple copy)
    * @param fromQuadruple   a byte array which contains the quadruple
    * 
    */
   public Quadruple(Quadruple fromQuadruple)
   {
       data = fromQuadruple.getQuadrupleByteArray();
       quadruple_length = fromQuadruple.getLength();
       quadruple_offset = 0;
       fldCnt = fromQuadruple.noOfFlds(); 
       fldOffset = fromQuadruple.copyFldOffset(); 
   }

   /**  
    * Class constructor
    * Creat a new quadruple with length = size,quadruple offset = 0.
    */
 
  public  Quadruple(int size)
  {
       // Creat a new quadruple
       data = new byte[size];
       quadruple_offset = 0;
       quadruple_length = size;     
  }
   
   /** Copy a quadruple to the current quadruple position
    *  you must make sure the quadruple lengths must be equal
    * @param fromQuadruple the quadruple being copied
    */
   public void quadrupleCopy(Quadruple fromQuadruple)
   {
       byte [] temparray = fromQuadruple.getQuadrupleByteArray();
       System.arraycopy(temparray, 0, data, quadruple_offset, quadruple_length);   
//       fldCnt = fromQuadruple.noOfFlds(); 
//       fldOffset = fromQuadruple.copyFldOffset(); 
   }

   /** This is used when you don't want to use the constructor
    * @param aquadruple  a byte array which contains the quadruple
    * @param offset the offset of the quadruple in the byte array
    * @param length the length of the quadruple
    */

   public void quadrupleInit(byte [] aquadruple, int offset, int length)
   {
      data = aquadruple;
      quadruple_offset = offset;
      quadruple_length = length;
   }

 /**
  * Set a quadruple with the given quadruple length and offset
  * @param	record	a byte array contains the quadruple
  * @param	offset  the offset of the quadruple ( =0 by default)
  * @param	length	the length of the quadruple
  */
 public void quadrupleSet(byte [] record, int offset, int length)  
  {
      System.arraycopy(record, offset, data, 0, length);
      quadruple_offset = 0;
      quadruple_length = length;
  }
  
 /** get the length of a quadruple, call this method if you did not 
  *  call setHdr () before
  * @return 	length of this quadruple in bytes
  */   
  public int getLength()
   {
      return quadruple_length;
   }

/** get the length of a quadruple, call this method if you did 
  *  call setHdr () before
  * @return     size of this quadruple in bytes
  */
  public short size()
   {
      return ((short) (fldOffset[fldCnt] - quadruple_offset));
   }
 
   /** get the offset of a quadruple
    *  @return offset of the quadruple in byte array
    */   
   public int getOffset()
   {
      return quadruple_offset;
   }   
   
   /** Copy the quadruple byte array out
    *  @return  byte[], a byte array contains the quadruple
    *		the length of byte[] = length of the quadruple
    */
    
   public byte [] getQuadrupleByteArray() 
   {
       byte [] quadruplecopy = new byte [quadruple_length];
       System.arraycopy(data, quadruple_offset, quadruplecopy, 0, quadruple_length);
       return quadruplecopy;
   }
   
   /** return the data byte array 
    *  @return  data byte array 		
    */
    
   public byte [] returnQuadrupleByteArray()
   {
       return data;
   }
   
   /**
    * Convert this field into integer 
    * 
    * @param	fldNo	the field number
    * @return		the converted integer if success
    *			
    * @exception   IOException I/O errors
    * @exception   FieldNumberOutOfBoundException Quadruple field number out of bound
    */

  public int getIntFld(int fldNo) 
  	throws IOException, FieldNumberOutOfBoundException
  {           
    int val;
    if ( (fldNo > 0) && (fldNo <= fldCnt))
     {
      val = Convert.getIntValue(fldOffset[fldNo -1], data);
      return val;
     }
    else 
     throw new FieldNumberOutOfBoundException (null, "TUPLE:TUPLE_FLDNO_OUT_OF_BOUND");
  }
    
   /**
    * Convert this field in to float
    *
    * @param    fldNo   the field number
    * @return           the converted float number  if success
    *			
    * @exception   IOException I/O errors
    * @exception   FieldNumberOutOfBoundException Quadruple field number out of bound
    */

    public float getFloFld(int fldNo) 
    	throws IOException, FieldNumberOutOfBoundException
     {
	float val;
      if ( (fldNo > 0) && (fldNo <= fldCnt))
       {
        val = Convert.getFloValue(fldOffset[fldNo -1], data);
        return val;
       }
      else 
       throw new FieldNumberOutOfBoundException (null, "TUPLE:TUPLE_FLDNO_OUT_OF_BOUND");
     }


   /**
    * Convert this field into String
    *
    * @param    fldNo   the field number
    * @return           the converted string if success
    *			
    * @exception   IOException I/O errors
    * @exception   FieldNumberOutOfBoundException Quadruple field number out of bound
    */

   public String getStrFld(int fldNo) 
   	throws IOException, FieldNumberOutOfBoundException 
   { 
         String val;
    if ( (fldNo > 0) && (fldNo <= fldCnt))      
     {
        val = Convert.getStrValue(fldOffset[fldNo -1], data, 
		fldOffset[fldNo] - fldOffset[fldNo -1]); //strlen+2
        return val;
     }
    else 
     throw new FieldNumberOutOfBoundException (null, "TUPLE:TUPLE_FLDNO_OUT_OF_BOUND");
  }
 
   /**
    * Convert this field into a character
    *
    * @param    fldNo   the field number
    * @return           the character if success
    *			
    * @exception   IOException I/O errors
    * @exception   FieldNumberOutOfBoundException Quadruple field number out of bound
    */

   public char getCharFld(int fldNo) 
   	throws IOException, FieldNumberOutOfBoundException 
    {   
       char val;
      if ( (fldNo > 0) && (fldNo <= fldCnt))      
       {
        val = Convert.getCharValue(fldOffset[fldNo -1], data);
        return val;
       }
      else 
       throw new FieldNumberOutOfBoundException (null, "TUPLE:TUPLE_FLDNO_OUT_OF_BOUND");
 
    }

  /**
   * Set this field to integer value
   *
   * @param	fldNo	the field number
   * @param	val	the integer value
   * @exception   IOException I/O errors
   * @exception   FieldNumberOutOfBoundException Quadruple field number out of bound
   */

  public Quadruple setIntFld(int fldNo, int val) 
  	throws IOException, FieldNumberOutOfBoundException
  { 
    if ( (fldNo > 0) && (fldNo <= fldCnt))
     {
	Convert.setIntValue (val, fldOffset[fldNo -1], data);
	return this;
     }
    else 
     throw new FieldNumberOutOfBoundException (null, "TUPLE:TUPLE_FLDNO_OUT_OF_BOUND"); 
  }

  /**
   * Set this field to float value
   *
   * @param     fldNo   the field number
   * @param     val     the float value
   * @exception   IOException I/O errors
   * @exception   FieldNumberOutOfBoundException Quadruple field number out of bound
   */

  public Quadruple setFloFld(int fldNo, float val) 
  	throws IOException, FieldNumberOutOfBoundException
  { 
   if ( (fldNo > 0) && (fldNo <= fldCnt))
    {
     Convert.setFloValue (val, fldOffset[fldNo -1], data);
     return this;
    }
    else  
     throw new FieldNumberOutOfBoundException (null, "TUPLE:TUPLE_FLDNO_OUT_OF_BOUND"); 
     
  }

  /**
   * Set this field to String value
   *
   * @param     fldNo   the field number
   * @param     val     the string value
   * @exception   IOException I/O errors
   * @exception   FieldNumberOutOfBoundException Quadruple field number out of bound
   */

   public Quadruple setStrFld(int fldNo, String val) 
		throws IOException, FieldNumberOutOfBoundException  
   {
     if ( (fldNo > 0) && (fldNo <= fldCnt))        
      {
         Convert.setStrValue (val, fldOffset[fldNo -1], data);
         return this;
      }
     else 
       throw new FieldNumberOutOfBoundException (null, "TUPLE:TUPLE_FLDNO_OUT_OF_BOUND");
    }


   /**
    * setHdr will set the header of this quadruple.   
    *
    * @param	numFlds	  number of fields
    * @param	types[]	  contains the types that will be in this quadruple
    * @param	strSizes[]      contains the sizes of the string 
    *				
    * @exception IOException I/O errors
    * @exception InvalidTypeException Invalid tupe type
    * @exception InvalidQuadrupleSizeException Quadruple size too big
    *
    */

public void setHdr (short numFlds,  AttrType types[], short strSizes[])
 throws IOException, InvalidTypeException, InvalidQuadrupleSizeException		
{
  if((numFlds +2)*2 > max_size)
    throw new InvalidQuadrupleSizeException (null, "TUPLE: TUPLE_TOOBIG_ERROR");
  
  fldCnt = numFlds;
  Convert.setShortValue(numFlds, quadruple_offset, data);
  fldOffset = new short[numFlds+1];
  int pos = quadruple_offset+2;  // start position for fldOffset[]
  
  //sizeof short =2  +2: array siaze = numFlds +1 (0 - numFilds) and
  //another 1 for fldCnt
  fldOffset[0] = (short) ((numFlds +2) * 2 + quadruple_offset);   
   
  Convert.setShortValue(fldOffset[0], pos, data);
  pos +=2;
  short strCount =0;
  short incr;
  int i;

  for (i=1; i<numFlds; i++)
  {
    switch(types[i-1].attrType) {
    
   case AttrType.attrInteger:
     incr = 4;
     break;

   case AttrType.attrReal:
     incr =4;
     break;

   case AttrType.attrString:
     incr = (short) (strSizes[strCount] +2);  //strlen in bytes = strlen +2
     strCount++;
     break;       
 
   default:
    throw new InvalidTypeException (null, "TUPLE: TUPLE_TYPE_ERROR");
   }
  fldOffset[i]  = (short) (fldOffset[i-1] + incr);
  Convert.setShortValue(fldOffset[i], pos, data);
  pos +=2;
 
}
 switch(types[numFlds -1].attrType) {

   case AttrType.attrInteger:
     incr = 4;
     break;

   case AttrType.attrReal:
     incr =4;
     break;

   case AttrType.attrString:
     incr =(short) ( strSizes[strCount] +2);  //strlen in bytes = strlen +2
     break;

   default:
    throw new InvalidTypeException (null, "TUPLE: TUPLE_TYPE_ERROR");
   }

  fldOffset[numFlds] = (short) (fldOffset[i-1] + incr);
  Convert.setShortValue(fldOffset[numFlds], pos, data);
  
  quadruple_length = fldOffset[numFlds] - quadruple_offset;

  if(quadruple_length > max_size)
   throw new InvalidQuadrupleSizeException (null, "TUPLE: TUPLE_TOOBIG_ERROR");
}
     
  
  /**
   * Returns number of fields in this quadruple
   *
   * @return the number of fields in this quadruple
   *
   */

  public short noOfFlds() 
   {
     return fldCnt;
   }

  /**
   * Makes a copy of the fldOffset array
   *
   * @return a copy of the fldOffset arrray
   *
   */

  public short[] copyFldOffset() 
   {
     short[] newFldOffset = new short[fldCnt + 1];
     for (int i=0; i<=fldCnt; i++) {
       newFldOffset[i] = fldOffset[i];
     }
     
     return newFldOffset;
   }

 /**
  * Print out the quadruple
  * @param type  the types in the quadruple
  * @Exception IOException I/O exception
  */
 public void print(AttrType type[])
    throws IOException 
 {
  int i, val;
  float fval;
  String sval;

  System.out.print("[");
  for (i=0; i< fldCnt-1; i++)
   {
    switch(type[i].attrType) {

   case AttrType.attrInteger:
     val = Convert.getIntValue(fldOffset[i], data);
     System.out.print(val);
     break;

   case AttrType.attrReal:
     fval = Convert.getFloValue(fldOffset[i], data);
     System.out.print(fval);
     break;

   case AttrType.attrString:
     sval = Convert.getStrValue(fldOffset[i], data,fldOffset[i+1] - fldOffset[i]);
     System.out.print(sval);
     break;
  
   case AttrType.attrNull:
   case AttrType.attrSymbol:
     break;
   }
   System.out.print(", ");
 } 
 
 switch(type[fldCnt-1].attrType) {

   case AttrType.attrInteger:
     val = Convert.getIntValue(fldOffset[i], data);
     System.out.print(val);
     break;

   case AttrType.attrReal:
     fval = Convert.getFloValue(fldOffset[i], data);
     System.out.print(fval);
     break;

   case AttrType.attrString:
     sval = Convert.getStrValue(fldOffset[i], data,fldOffset[i+1] - fldOffset[i]);
     System.out.print(sval);
     break;

   case AttrType.attrNull:
   case AttrType.attrSymbol:
     break;
   }
   System.out.println("]");

 }

  /**
   * private method
   * Padding must be used when storing different types.
   * 
   * @param	offset
   * @param type   the type of quadruple
   * @return short typle
   */

private short pad(short offset, AttrType type)
{
    return 0;
}
}

