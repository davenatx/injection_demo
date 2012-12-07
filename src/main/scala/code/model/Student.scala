/*
* Copyright 2012 David Price
*
* This file is part of InjectionDemo.
*
* InjectionDemo is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* any later version.
*
* InjectionDemo is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with InjectionDemo.  If not, see <http://www.gnu.org/licenses/>.
*/

package com.dmp.model

import net.liftweb.mapper._
import net.liftweb.common.Full
import net.liftweb.util.Helpers._

object Student extends Student with LongKeyedMetaMapper[Student] {

  override def dbTableName = "students"

  override def fieldOrder = List(id, updatedAt, createdAt, studentIdNum, ssn, userId, password, firstName,
    lastName, birthDate, classLevel)

  override def dbAddTable = Full(populate _)

  // Create sample data
  private def populate {
    Student.create
      .studentIdNum("8756532")
      .ssn("666-66-5555")
      .userId("twood2")
      .password("password")
      .firstName("Todd")
      .lastName("Wood")
      .birthDate(dateFormatter.parse("1978/01/01"))
      .classLevel(Class.Sophomore)
      .save
    Student.create
      .studentIdNum("3652145")
      .ssn("222-22-2222")
      .userId("dprice1")
      .password("password")
      .firstName("David")
      .lastName("Price")
      .birthDate(dateFormatter.parse("1982/04/01"))
      .classLevel(Class.Senior)
      .save
    Student.create
      .studentIdNum("4125325")
      .ssn("333-33-3333")
      .userId("jsmith7")
      .password("password")
      .firstName("John")
      .lastName("Smith")
      .birthDate(dateFormatter.parse("1979/02/03"))
      .classLevel(Class.Junior)
      .save
    Student.create
      .studentIdNum("3548798")
      .ssn("444-44-5555")
      .userId("jdoe3")
      .password("password")
      .firstName("Jane")
      .lastName("Doe")
      .birthDate(dateFormatter.parse("1985/01/01"))
      .classLevel(Class.Freshman)
      .save
  }
}

class Student extends LongKeyedMapper[Student] with CreatedUpdated with IdPK with OneToMany[Long, Student] {

  def getSingleton = Student

  object studentIdNum extends MappedString(this, 10) {
    override def dbIndexed_? = true
  }

  object userId extends MappedString(this, 10)

  // password should extend MappedPassword however MappedString makes for an easier demo
  object password extends MappedString(this, 10)

  object firstName extends MappedString(this, 25)

  object lastName extends MappedString(this, 25)

  object birthDate extends MappedDate(this)

  object classLevel extends MappedEnum(this, Class)

  object ssn extends MappedString(this, 11)

  object payments extends MappedOneToMany(Payment, Payment.student)

}

object Class extends Enumeration {
  val Freshman, Sophomore, Junior, Senior = Value
}