module com.example.shiftmate {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.shiftmate to javafx.fxml;
    opens com.example.shiftmate.Controllers.Employee to javafx.fxml;
    opens com.example.shiftmate.Controllers.Program to javafx.fxml;
    opens com.example.shiftmate.Controllers.Schedule to javafx.fxml;
    opens com.example.shiftmate.Models to javafx.fxml;
    opens com.example.shiftmate.Controllers.Main to javafx.fxml;

    exports com.example.shiftmate.Controllers.Employee;
    exports com.example.shiftmate.Controllers.Program;
    exports com.example.shiftmate;
    exports com.example.shiftmate.Controllers.Schedule;
    exports com.example.shiftmate.Models;
    exports com.example.shiftmate.Controllers.Main;

}
