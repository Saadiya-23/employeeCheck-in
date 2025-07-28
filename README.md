# employeeCheck-in

```
Data Classes:
  --data class EmployeeDetails(    // to store the overall emp data 
    val id: String,
    val firstName: String,
    val lastName: String,
    val role: String,
    val department: String
  )
  --data class AttendanceEntry(   // to store the emp checked in after passing all the validation (valid user)
    val id: String ,
    val date: String,
    val time: String
)

Input :
  Emp_Id :String        // Emp id as input from the user 

 val attendanceList=mutableListOf<AttendanceEntry>()    // to get the list of details of all the  employees who have succesfully checked in

 Validation{
       
 // contains all the validation function to check whether the emp can be checked in

  isEmployeeExist(String id):boolean{   // to check whether the employee is valid
    if(id in EmployeeDetails) {
        return true
    }
    return false
  }

  isCheckedIn(String id ,  attendanceList:mutableListOf<AttendanceEntry>()): boolean {   // to check whether the employee has checked in on that day
      val today=getData()
     return attendanceList.any{ it.id==id && it.date==today }
  }
}


Check-In(String id){    // main logic for check in 
  if(!isEmployeeExist(id)){      // if emp id does not exist 
    "Emp not found"
  }
  else {             // if emp is present 
    if(isCheckedIn(id)){  // if he has checked in for that day 
        "you have checked in already!"
    }
    else {          // if he hasnt checked in 
        Date = getDate()
        Time=getTime()
        AttendanceDetails.add(id,Time,Date)    //adding the emp details to the entry 
    }
}





```
