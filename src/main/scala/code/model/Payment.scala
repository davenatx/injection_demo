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
import java.math.MathContext

object Payment extends Payment with LongKeyedMetaMapper[Payment] {
  override def dbTableName = "payments"

  override def fieldOrder = List(id, updatedAt, createdAt, amount, paymentDate, nameOnCard, cardType, cardNumber,
    cardExpDate, account, student)

  override def dbAddTable = Full(populate _)

  // Create sample data
  private def populate {
    Payment.create
      .amount(2000.00)
      .paymentDate(dateFormatter.parse("2012/06/13"))
      .nameOnCard("Todd Wood")
      .cardType(CreditCards.Visa)
      .cardNumber("4876453423456544")
      .cardExpDate(dateFormatter.parse("2014/07/01"))
      .account(Account.StudentAccount)
      .student(1)
      .save
    Payment.create
      .amount(2000.00)
      .paymentDate(dateFormatter.parse("2012/06/01"))
      .nameOnCard("David Price")
      .cardType(CreditCards.Visa)
      .cardNumber("4610461229539750")
      .cardExpDate(dateFormatter.parse("2014/01/01"))
      .account(Account.StudentAccount)
      .student(2)
      .save
    Payment.create
      .amount(95.00)
      .paymentDate(dateFormatter.parse("2012/04/15"))
      .nameOnCard("David Price")
      .cardType(CreditCards.Visa)
      .cardNumber("4610461229539750")
      .cardExpDate(dateFormatter.parse("2014/01/01"))
      .account(Account.MonthlyPaymentPlan)
      .student(2)
      .save
    Payment.create
      .amount(54.95)
      .paymentDate(dateFormatter.parse("2012/02/05"))
      .nameOnCard("David Price")
      .cardType(CreditCards.Visa)
      .cardNumber("4610461229539750")
      .cardExpDate(dateFormatter.parse("2014/01/01"))
      .account(Account.Other)
      .student(2)
      .save

    Payment.create
      .amount(2495.00)
      .paymentDate(dateFormatter.parse("2012/05/25"))
      .nameOnCard("John Smith")
      .cardType(CreditCards.AmericanExpress)
      .cardNumber("3715425887458655")
      .cardExpDate(dateFormatter.parse("2015/01/01"))
      .account(Account.StudentAccount)
      .student(3)
      .save
    Payment.create
      .amount(600.00)
      .paymentDate(dateFormatter.parse("2012/03/25"))
      .nameOnCard("John Smith")
      .cardType(CreditCards.AmericanExpress)
      .cardNumber("3715425887458655")
      .cardExpDate(dateFormatter.parse("2014/01/01"))
      .account(Account.EdPay)
      .student(3)
      .save

    Payment.create
      .amount(325.00)
      .paymentDate(dateFormatter.parse("2012/01/15"))
      .nameOnCard("Jane Doe")
      .cardType(CreditCards.Discover)
      .cardNumber("6011548774545487")
      .cardExpDate(dateFormatter.parse("2014/07/01"))
      .account(Account.Other)
      .student(4)
      .save
  }
}

class Payment extends LongKeyedMapper[Payment] with CreatedUpdated with IdPK {

  def getSingleton = Payment

  object amount extends MappedDecimal(this, MathContext.DECIMAL64, 2)

  object paymentDate extends MappedDate(this)

  object nameOnCard extends MappedString(this, 50)

  object cardType extends MappedEnum(this, CreditCards)

  // Should be encrypted, left in plain text for demo purposes
  object cardNumber extends MappedString(this, 16)

  object cardExpDate extends MappedDate(this)

  object account extends MappedEnum(this, Account)

  object student extends MappedLongForeignKey(this, Student) {
    override def dbColumnName = "student_id"
  }

}

object CreditCards extends Enumeration {
  val Visa, Mastercard, AmericanExpress, Discover = Value
}

object Account extends Enumeration {
  val StudentAccount, MonthlyPaymentPlan, EdPay, Other = Value
}