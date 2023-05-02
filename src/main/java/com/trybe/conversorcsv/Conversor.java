package com.trybe.conversorcsv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Classe Conversor.
 */
public class Conversor {

  /**
   * Função utilizada apenas para validação da solução do desafio.
   *
   * @param args Não utilizado.
   * @throws IOException Caso ocorra algum problema ao ler os arquivos de entrada ou
   *                     gravar os arquivos de saída.
   */

  public static void main(String[] args) throws IOException {
    File pastaDeEntradas = new File("./entradas/");
    File pastaDeSaidas = new File("./saidas/");
    new Conversor().converterPasta(pastaDeEntradas, pastaDeSaidas);
  }

  /**
   * Converte todos os arquivos CSV da pasta de entradas. Os resultados são gerados
   * na pasta de saídas, deixando os arquivos originais inalterados.
   *
   * @param pastaDeEntradas Pasta contendo os arquivos CSV gerados pela página web.
   * @param pastaDeSaidas Pasta em que serão colocados os arquivos gerados no formato
   *                      requerido pelo subsistema.
   *
   * @throws IOException Caso ocorra algum problema ao ler os arquivos de entrada ou
   *                     gravar os arquivos de saída.
   */

  public void converterPasta(File pastaDeEntradas, File pastaDeSaidas) throws IOException {
    if (!pastaDeEntradas.isDirectory() || !pastaDeEntradas.canRead()) {
      throw new IllegalArgumentException("Pasta de entradas inválida.");
    }

    if (!pastaDeSaidas.exists()) {
      pastaDeSaidas.mkdirs();
    }

    for (File inputFile : pastaDeEntradas.listFiles()) {
      if (inputFile.isFile() && inputFile.canRead() && inputFile.getName().endsWith(".csv")) {
        processFile(inputFile, pastaDeSaidas);
      }
    }
  }

  private void processFile(File inputFile, File pastaDeSaidas) throws IOException {
    String state = inputFile.getName();
    ArrayList<String> outputLines = new ArrayList<>();

    try (BufferedReader bufferedReader = new BufferedReader(new FileReader(inputFile))) {
      String lineContent = bufferedReader.readLine();
      while (lineContent != null) {
        if (!lineContent.startsWith("Nome")) {
          String formattedLine = convertLine(lineContent);
          outputLines.add(formattedLine);
        }
        lineContent = bufferedReader.readLine();
      }
    } catch (IOException e) {
      throw new RuntimeException("Erro ao ler arquivo de entrada.", e);
    }

    if (!outputLines.isEmpty()) {
      writeFileContent(outputLines, pastaDeSaidas, state);
    }
  }

  private String convertLine(String lineContent) {
    String[] columns = lineContent.split(",");

    String name = columns[0].toUpperCase();
    String birthDate = convertDate(columns[1]);
    String mail = columns[2];
    String cpf = convertCpf(columns[3]);

    return name + "," + birthDate + "," + mail + "," + cpf;
  }

  private String convertDate(String brDate) {
    SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    Date date;

    try {
      date = inputDateFormat.parse(brDate);
    } catch (ParseException e) {
      throw new RuntimeException("Erro ao converter data de nascimento.", e);
    }

    SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy/MM/dd");
    return outputDateFormat.format(date).replace("/", "-");
  }

  private String convertCpf(String cpf) {
    return cpf.substring(0, 3) + "." + cpf.substring(3, 6)
        + "." + cpf.substring(6, 9) + "-" + cpf.substring(9);
  }

  private void writeFileContent(
      ArrayList<String> lines, File pastaDeSaidas, String state) throws IOException {
    String csvFileName = pastaDeSaidas.getAbsolutePath() + File.separator + state;

    try (FileWriter csvWriter = new FileWriter(csvFileName)) {
      csvWriter.append("Nome completo,Data de nascimento,Email,CPF\n");
      for (String line : lines) {
        csvWriter.append(line).append("\n");
      }
    } catch (IOException e) {
      throw new RuntimeException("Erro ao gravar arquivo de saída.", e);
    }
  }

}
