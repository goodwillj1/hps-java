#include <TFile.h>
//#include <TNtuple.h>
#include <TH1.h>
#include <TH2.h>
//#include <TProfile.h>
#include <TCanvas.h>
#include <TLegend.h>
//#include <TFrame.h>
//#include <TROOT.h>
//#include <TSystem.h>
//#include <TRandom3.h>
#include "TRint.h"
#include "TROOT.h"
#include "TKey.h"
#include "TDirectory.h"
#include <TBenchmark.h>
#include <TInterpreter.h>
#include <iostream>
using namespace std;


void mu() {
    TFile *MyFile = new TFile("out.root","READ");


    TString hname ="mu-";
    TKey *key = MyFile->FindKey(hname);
    TH1 *OriginZ = (TH1*)MyFile->Get(hname + "/ZOrigin");
    TH1 *xyScore = (TH1*)MyFile->Get(hname + "/xVsyScore");
    TH1 *xyECal = (TH1*)MyFile->Get(hname + "/xVsyECal");
    TH1 *dxdy = (TH1*)MyFile->Get(hname +"/DelxVsDelY");
    TH1 *tdxdy = (TH1*)MyFile->Get(hname +"/TimeVsDelMag");
    TCanvas *c1 = new TCanvas("c1","mu-",1000,1000);
    c1->Divide(2,3);
    c1->cd(1);
    OriginZ->Draw();
    c1->cd(2);
    xyScore->Draw("COLZ");
    c1->cd(3);
    xyECal->Draw("COLZ");
    c1->cd(4);
    dxdy->Draw("COLZ");
    c1->cd(5);
    tdxdy->Draw("COLZ");
    c1->Print("mu-.pdf(","pdf");

    TH1 *thetaX = (TH1*)MyFile->Get(hname +"/thetaX");
    TH1 *thetaY = (TH1*)MyFile->Get(hname +"/thetaY");
    TH1 *energy = (TH1*)MyFile->Get(hname +"/energy");
    TH1 *momentum = (TH1*)MyFile->Get(hname +"/momentum");
    TH1 *numHits = (TH1*)MyFile->Get(hname +"/numHits");

    c1 = new TCanvas("c1","mu-",1000,1000);
    c1->Divide(2,3);
    c1->cd(1);
    thetaX->Draw();
    c1->cd(2);
    thetaY->Draw();
    c1->cd(3);
    energy->Draw();
    c1->cd(4);
    momentum->Draw();
    c1->cd(5);
    numHits->Draw();
    c1->Print("mu-.pdf)","pdf");


    hname = "mu+";
    OriginZ = (TH1*)MyFile->Get(hname + "/ZOrigin");
    xyScore = (TH1*)MyFile->Get(hname + "/xVsyScore");
    xyECal = (TH1*)MyFile->Get(hname + "/xVsyECal");
    dxdy = (TH1*)MyFile->Get(hname +"/DelxVsDelY");
    tdxdy = (TH1*)MyFile->Get(hname +"/TimeVsDelMag");

    c1 = new TCanvas("c1","mu+",1000,1000);
    c1->Divide(2,3);
    c1->cd(1);
    OriginZ->Draw();
    c1->cd(2);
    xyScore->Draw("COLZ");
    c1->cd(3);
    xyECal->Draw("COLZ");
    c1->cd(4);
    dxdy->Draw("COLZ");
    c1->cd(5);
    tdxdy->Draw("COLZ");
    c1->Print("mu+.pdf(","pdf");



    thetaX = (TH1*)MyFile->Get(hname +"/thetaX");
    thetaY = (TH1*)MyFile->Get(hname +"/thetaY");
    energy = (TH1*)MyFile->Get(hname +"/energy");
    momentum = (TH1*)MyFile->Get(hname +"/momentum");
    numHits = (TH1*)MyFile->Get(hname +"/numHits");

    c1 = new TCanvas("c1","mu+",1000,1000);
    c1->Divide(2,3);
    c1->cd(1);
    thetaX->Draw();
    c1->cd(2);
    thetaY->Draw();
    c1->cd(3);
    energy->Draw();
    c1->cd(4);
    momentum->Draw();
    c1->cd(5);
    numHits->Draw();
    c1->Print("mu+.pdf)","pdf");
}