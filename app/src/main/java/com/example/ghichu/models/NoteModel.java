package com.example.ghichu.models;


public class NoteModel {
        private Integer id;
        private String title, notes, img, timeCreate,userId;
        private Boolean pinned;

        public NoteModel() {
            this.img="";
            this.title="";
            this.pinned=false;
            this.notes="";
        }

    public NoteModel(String title, String notes, String img, String timeCreate, Boolean pinned) {
        this.title = title;
        this.img = img;
        this.notes = notes;
        this.timeCreate = timeCreate;
        this.pinned = pinned;
    }

        public NoteModel(Integer id, String title, String notes, String img, String timeCreate, Boolean pinned, String userId) {
            this.id = id;
            this.title = title;
            this.img = img;
            this.notes = notes;
            this.timeCreate = timeCreate;
            this.pinned = pinned;
            this.userId = userId;
        }

        public Integer getId() {
            return this.id;
        }

        public void setId(Integer Id) {
            this.id = Id;
        }

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getNotes() {
            return this.notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public String getImg() {
            return this.img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getTimeCreate() {
            return this.timeCreate;
        }

        public void setTimeCreate(String timeCreate) {
            this.timeCreate = timeCreate;
        }

        public String getUserId() {
        return this.userId;
    }

        public void setUserId(String userId) {
        this.userId = userId;
    }

        public Boolean getPinned() {
            return this.pinned;
        }

        public void setPinned(Boolean pinned) {
            this.pinned = pinned;
        }
}
