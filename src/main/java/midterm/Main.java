package midterm;

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Kadir Kartal
 */

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

class Main {
  private static class EmailTransporter {
    String host;
    String port;
    String userName;
    Session session;

    EmailTransporter(String host, String port, String userName, String password) {
      this.host = host;
      this.port = port;
      this.userName = userName;
      Properties properties = System.getProperties();
      properties.put("mail.smtp.host", this.host);
      properties.put("mail.smtp.port", this.port);
      properties.put("mail.smtp.starttls.enable", "true");
      properties.put("mail.smtp.auth", "true");

      this.session = Session.getInstance(properties, new javax.mail.Authenticator() {

        protected PasswordAuthentication getPasswordAuthentication() {
          return new PasswordAuthentication(userName, password);
        }

      });
      this.session.setDebug(false);
    }

    public void sendMail(String to, String subject, String body) {

      try {
        MimeMessage message = new MimeMessage(this.session);
        message.setFrom(new InternetAddress(this.userName));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setText(body);
        System.out.println("Email gönderiliyor...");
        Transport.send(message);
        System.out.println("Email başarıyla gönderildi.");
      } catch (MessagingException mex) {
        System.err.println("Bir hata oluştu. Email gönderilemedi.");
      }

    }

  }

  private static class EliteMember extends Member {
    EliteMember(String name, String surname, String email) {
      super(name, surname, email);
    }
    public String toString() {
      return String.format("Elite Member: %s\t%s\t%s", this.name, this.surname, this.email);
    }
  }

  private static class Member {
    String name;
    String surname;
    String email;
    Member(String name, String surname, String email) {
      this.name = name;
      this.surname = surname;
      this.email = email;
    }
    public String toString() {
      return String.format("General Member: %s\t%s\t%s", this.name, this.surname, this.email);
    }
  }

  private static void readMembers(ArrayList<Member> members, ArrayList<EliteMember> eliteMembers) {
    try {
      File file = new File("src/kullanicilar.txt");
      Scanner fileReader = new Scanner(file);
      boolean readingElites = false;
      boolean readingGenerals = false;
      while (fileReader.hasNextLine()) {
        String line = fileReader.nextLine();
        if(line.equals("ELITE")) {
          readingElites = true;
          readingGenerals = false;
        } else if(line.equals("GENERAL")) {
          readingGenerals = true;
          readingElites = false;
        } else if(readingGenerals) {
          String[] items = line.split("\t");
          members.add(new Member(items[0], items[1], items[2]));
        } else if(readingElites) {
          String[] items = line.split("\t");
          eliteMembers.add(new EliteMember(items[0], items[1], items[2]));
        }
      }
      fileReader.close();
    } catch (FileNotFoundException e) {
      System.out.println("Üyeler dosyası \"kullanicilar.txt\" okunamıyor.");
    }
  }

  private static void writeMembers(ArrayList<Member> members, ArrayList<EliteMember> eliteMembers) {
    try {
      FileWriter file = new FileWriter("src/kullanicilar.txt");
      file.write("ELITE\n");
      for(int i = 0; i < eliteMembers.size(); i++) {
        EliteMember currentMember = eliteMembers.get(i);
        file.write(currentMember.name + "\t" + currentMember.surname + "\t" + currentMember.email + "\n");
      }
      file.write("GENERAL\n");
      for(int i = 0; i < members.size(); i++) {
        Member currentMember = members.get(i);
        file.write(currentMember.name + "\t" + currentMember.surname + "\t" + currentMember.email + "\n");
      }
      file.close();
    } catch (IOException e) {
      System.out.println("Üyeleri dosyaya yazarken bir hata oluştu.");
    }
  }

  public static void main(String[] args) {
    ArrayList<Member> members = new ArrayList<Member>();
    ArrayList<EliteMember> eliteMembers = new ArrayList<EliteMember>();
    readMembers(members, eliteMembers);
    EmailTransporter emailTransporter = new EmailTransporter("smtp-mail.outlook.com", "587", "testemail@outlook.com", "testpassword123");
    try (Scanner scanner = new Scanner(System.in)) {
      while (true) {
        System.out.println("1- Elit üye ekleme\n2- Genel üye ekleme\n3- Mail Gönderme\n4- Uygulamadan çık");
        String optionMenu1 = scanner.nextLine();
        if (optionMenu1.equals("1")) {
          System.out.print("İsim: ");
          String name = scanner.nextLine();
          System.out.print("Soysim: ");
          String surname = scanner.nextLine();
          System.out.print("Email: ");
          String email = scanner.nextLine();
          eliteMembers.add(new EliteMember(name, surname, email));
          System.out.println(eliteMembers.get(eliteMembers.size() - 1));
        } else if (optionMenu1.equals("2")) {
          System.out.print("İsim: ");
          String name = scanner.nextLine();
          System.out.print("Soysim: ");
          String surname = scanner.nextLine();
          System.out.print("Email: ");
          String email = scanner.nextLine();
          members.add(new Member(name, surname, email));
          System.out.println(members.get(members.size() - 1));
        } else if (optionMenu1.equals("3")) {
          System.out.println("1- Elit üyelere mail\n2- Genel üyelere mail\n3- Tüm üyelere mail");
          String optionMenu2 = scanner.nextLine();
          if (optionMenu2.equals("1")) {
            System.out.print("Mailin Konusu: ");
            String subject = scanner.nextLine();
            System.out.print("Mailin İçeriği: ");
            String body = scanner.nextLine();
            for (int i = 0; i < eliteMembers.size(); i++) {
              EliteMember currentMember = eliteMembers.get(i);
              emailTransporter.sendMail(currentMember.email, subject, body);
            }
          } else if (optionMenu2.equals("2")) {
            System.out.print("Mailin Konusu: ");
            String subject = scanner.nextLine();
            System.out.print("Mailin İçeriği: ");
            String body = scanner.nextLine();
            for (int i = 0; i < members.size(); i++) {
              Member currentMember = members.get(i);
              emailTransporter.sendMail(currentMember.email, subject, body);
            }
          } else if (optionMenu2.equals("3")) {
            System.out.print("Mailin Konusu: ");
            String subject = scanner.nextLine();
            System.out.print("Mailin İçeriği: ");
            String body = scanner.nextLine();
            for (int i = 0; i < eliteMembers.size(); i++) {
              EliteMember currentMember = eliteMembers.get(i);
              emailTransporter.sendMail(currentMember.email, subject, body);
            }
            for (int i = 0; i < members.size(); i++) {
              Member currentMember = members.get(i);
              emailTransporter.sendMail(currentMember.email, subject, body);
            }
          } else {
            System.out.println("Yanlış tuş girildi.");
          }
        } else if (optionMenu1.equals("4")) {
          System.out.println("Bizi tercih ettiğiniz için teşekkür ederiz.");
          break;
        } else {
          System.out.println("Yanlış tuş girildi.");
        }
      }
    }
    writeMembers(members, eliteMembers);
  }
}
