import java.util.Objects;

class Address{
    private final String street;
    private final String city;

    public Address(String street, String city) {
        this.street = street;
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(street, address.street) &&
                Objects.equals(city, address.city);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city);
    }

    @Override
    public String toString() {
        return "Address{" +
                "street='" + street + '\'' +
                ", city='" + city + '\'' +
                '}';
    }
}
enum Gender{
    MALE,FEMALE
}
class Person(){
    private String firstName=null;
    private String lastName=null;
    private Person father=null;
    private Person mother=null;
    private Date birthDate=null;
    private Gender gender=MALE;
    private Address address=null;

    public Person(String firstName, String lastName, Person father, Person mother, Date birthDate, Gender gender, Address address) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.father = father;
        this.mother = mother;
        this.birthDate = birthDate;
        this.gender = gender;
        this.address = address;
    }

    public Person() {
        firstName=null;
        lastName=null;
        father=null;
        mother=null;
        birthDate=null;
        gender=MALE;
    }

    public String getFirstName() {
        return firstName;
    }

    public Person setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public Person setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public Person getFather() {
        return father;
    }

    public Person setFather(Person father) {
        this.father = father;
        return this;
    }

    public Person getMother() {
        return mother;
    }

    public Person setMother(Person mother) {
        this.mother = mother;
        return this;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public Person setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
        return this;
    }

    public Gender getGender() {
        return gender;
    }

    public Person setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public Address getAddress() {
        return address;
    }

    public Person setAddress(Address address) {
        this.address = address;
        return this;
    }
}
public class ModuleClass {
    public static void main(String[]) {
        //Person samy = new Person();
        //samy.firstName = "samy";
        //samy.gender = MALE
        Person samy = new Person("samy", null,null,null,null,Gender.MALE,new Address("No","Where"));
        Person monia =new Person("monia", null,null,null,null,Gender.FEMALE,new Address("No","Where"));
        Person salim = new Person("salim", "kilani", samy, monia, t "2000-01-01", Gender.MALE, Address("Arafet St", "Sousse"));
        Person leila =new Person(null,salim.getLastName(),salim.getFather(),salim.getMother(),salim.getBirthDate(),Gender.MALE, new Address("No","Where"));
        leila.firstName = "sarra";
        leila.gender = FEMALE
        Person leila =new Person("amani",salim.getLastName(),salim.getFather(),salim.getMother(),salim.getBirthDate(),Gender.FEMALE,salim.getAddress());
    }
}
