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

package com.dmp.snippet

import net.liftweb.util.Helpers._
import net.liftweb.common.Logger
import xml.{ NodeSeq }
import net.liftmodules.widgets.tablesorter._
import net.liftweb.http.{ LiftRules, S, SHtml }

class User extends Logger {

  // Bind the user details
  def getDetails(xhtml: NodeSeq): NodeSeq = {
    import com.dmp.snippet.currentStudentVar
    bind("detail", xhtml,
      "name" -> String.format("%s %s", currentStudentVar.is.firstName, currentStudentVar.is.lastName),
      "birthDate" -> currentStudentVar.is.birthDate,
      "ssn" -> currentStudentVar.is.ssn,
      "studentIdNum" -> currentStudentVar.is.studentIdNum,
      "classLevel" -> currentStudentVar.is.classLevel)
  }

  // Build the payments table
  def getPayments(xhtml: NodeSeq): NodeSeq = {

    // flatmap the payments list and bind payment values to table
    def bindPayments(xhtml: NodeSeq): NodeSeq = {
      import com.dmp.snippet.currentStudentVar
      currentStudentVar.is.payments.flatMap {
        payment =>
          bind("payment", xhtml,
            "paymentDate" -> payment.paymentDate,
            "amount" -> String.format("$%s", payment.amount.is.toString),
            "account" -> payment.account,
            "cardType" -> payment.cardType,
            "cardNumber" -> payment.cardNumber,
            "cardExpDate" -> payment.cardExpDate,
            "nameOnCard" -> payment.nameOnCard)
      }
    }
    // bind the list of payments
    bind("paymentList", xhtml,
      "list" -> bindPayments _)
  }

  // Enable TableSorter
  def tableSorter(xhtml: NodeSeq): NodeSeq = {
    val headers = (0, DisableSorting()) :: (8, Sorter("paymentDate")) :: Nil
    val sortList = (8, Sorting.DSC) :: Nil
    val options = TableSorter.options(headers, sortList)
    TableSorter("#myTable", options)
  }

  // Override TableSorter CSS
  def tableSorterCSS(xhtml: NodeSeq): NodeSeq = {
    // Override default CSS served with TableSorter
    <head>
      <link rel="stylesheet" href={ "css/tablestyle.css" } type="text/css"/>
    </head>
  }

  // Logout
  def logout(xhtml: NodeSeq): NodeSeq = {
    bind("logout", xhtml,
      "submit" -> SHtml.submit("Logout", doLogout, "class" -> "button"))
  }

  def doLogout() = {
    S.redirectTo("/index")
  }

}