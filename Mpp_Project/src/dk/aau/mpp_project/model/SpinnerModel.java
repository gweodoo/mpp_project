package dk.aau.mpp_project.model;

public class SpinnerModel {
     
        private  String User="";
        private  String Image="";
         
        /*********** Set Methods ******************/
        public void setUser(String userName)
        {
            this.User = userName;
        }
       
        public void setImage(String Image)
        {
            this.Image = Image;
        }
         
        /*********** Get Methods ****************/
        public String getUserName()
        {
            return this.User;
        }
         
        public String getImage()
        {
            return this.Image;
        }

  }