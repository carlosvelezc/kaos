/* ----------------------------------------------------------------------------
 * This file was automatically generated by SWIG (http://www.swig.org).
 * Version 2.0.7
 *
 * Do not make changes to this file unless you know what you are doing--modify
 * the SWIG interface file instead.
 * ----------------------------------------------------------------------------- */

package edu.upc.freeling;

public class Tokenizer {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected Tokenizer(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(Tokenizer obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        freelingJNI.delete_Tokenizer(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public Tokenizer(String arg0) {
    this(freelingJNI.new_Tokenizer(arg0), true);
  }

  public ListWord tokenize(String arg0) {
    return new ListWord(freelingJNI.Tokenizer_tokenize__SWIG_0(swigCPtr, this, arg0), true);
  }

  public ListWord tokenize(String arg0, SWIGTYPE_p_unsigned_long arg1) {
    return new ListWord(freelingJNI.Tokenizer_tokenize__SWIG_1(swigCPtr, this, arg0, SWIGTYPE_p_unsigned_long.getCPtr(arg1)), true);
  }

}