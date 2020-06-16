void mu() {

    TFile *MyFile = new TFile("out.root","READ");

    TH1 *OriginZ = (TH1*)MyFile->Get("mu-/ZOrigin");
    TH1 *xyScore = (TH1*)MyFile->Get("mu-/xVsyScore");
    TH1 *xyECal = (TH1*)MyFile->Get("mu-/xVsyECal");
    TH1 *dxdy = (TH1*)MyFile->Get("mu-/DelxVsDelY");
    TH1 *tdMag= (TH1*)MyFile->Get("mu-/TimeVsDelMag");
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
    tdMag->Draw("COLZ");

    c1->SaveAs("mu2.pdf","pdf");
}