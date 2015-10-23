package com.remarkmedia.reactive.hbase

import java.util.Date

import org.apache.hadoop.hbase.util.Bytes

/**
 * Created by hanrenwei on 10/23/15.
 */
object BytesConversions {
  implicit def string2Bytes(in: String) = Bytes.toBytes(in)
  implicit def long2Bytes(in: Long) = Bytes.toBytes(in)
  implicit def date2Bytes(in: Date) = Bytes.toBytes(in.getTime)
  implicit def date2Bytes(in: Int) = Bytes.toBytes(in)
  implicit def double2Bytes(in: Double) = Bytes.toBytes(in)
  implicit def boolean2Bytes(in: Boolean) = Bytes.toBytes(in)
}