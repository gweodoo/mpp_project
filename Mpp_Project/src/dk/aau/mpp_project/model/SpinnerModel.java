package dk.aau.mpp_project.model;

public class SpinnerModel {
     
        private  String User="";
        private  String Image=""; 
        private  String Url="";
         
        /*********** Set Methods ******************/
        public void setUser(String userName)
        {
            this.User = userName;
        }
         
        public void setImage(String Image)
        {
            this.Image = Image;
        }
         
        public void setUrl(String Url)
        {
            this.Url = Url;
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
     
        public String getUrl()
        {
            return this.Url;
        }   
  }